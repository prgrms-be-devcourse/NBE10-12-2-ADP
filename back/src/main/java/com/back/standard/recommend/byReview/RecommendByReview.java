package com.back.standard.recommend.byReview;

import com.back.standard.recommend.util.CosineSimilarityCalcer;
import com.back.standard.recommend.util.SimilarityCalcer;
import com.back.standard.recommend.util.Vector;

import java.util.*;

public class RecommendByReview {

    public record RecommendReview (
            long reviewerId,
            long subjectId,
            float rating
    ) { }

    static class Reviewer {

        Map<Long, Float> ratings = new HashMap<>();
        float ratingSum = 0;

        List<Vector.VectorElement> getNormalizedRatings() {
            return ratings.entrySet().stream()
                    .map(entry ->
                            new Vector.VectorElement(entry.getKey(),
                                    entry.getValue() - (ratingSum / ratings.size())))
                    .toList();
        }

        void addRating(long targetId, float rating) {
            if (ratings.containsKey(targetId)) {
                ratingSum -= ratings.get(targetId);
            }
            ratings.put(targetId, rating);
            ratingSum += rating;
        }

    }

    record Similar (
            long id,
            double score
    ) { }

    private final Map<Long, Reviewer> users;
    private SimilarityCalcer calcer = new CosineSimilarityCalcer();

    public RecommendByReview() {
        users = new HashMap<>();
    }

    public RecommendByReview(SimilarityCalcer calcer) {
        users = new HashMap<>();
        this.calcer = calcer;
    }

    public void clear() {
        users.clear();
    }

    public void setData(List<RecommendReview> reviews) {

        reviews.forEach(review -> {
            if (!users.containsKey(review.reviewerId())) {
                users.put(review.reviewerId(), new Reviewer());
            }
            users.get(review.reviewerId()).addRating(
                    review.subjectId(), review.rating());

        });

    }

    public List<Long> getRecommend(long targetUserId, int referenceCnt, int maxRecommend) {

        Reviewer targetUser = users.getOrDefault(targetUserId, null);

        if (targetUser == null) return List.of();
        if (targetUser.ratings.isEmpty()) return List.of();

        calcer.setVectorA(targetUser.getNormalizedRatings());

        List<Similar> similars = new ArrayList<>();

        users.entrySet().stream()
                .filter(set -> set.getValue() != targetUser)
                .filter(set -> !set.getValue().ratings.isEmpty())
                .forEach(set -> {
                    calcer.setVectorB(set.getValue().getNormalizedRatings());
                    similars.add(new Similar(set.getKey(), calcer.getCosineSimilarity()));
                });

        similars.sort(Comparator.comparingDouble(a -> -a.score()));

        Map<Long, Double> recommends = new HashMap<>();
        Set<Long> alreadyRead = targetUser.ratings.keySet();

        for (int i = 0; i < referenceCnt && i < similars.size(); i++) {

            double similarScore = similars.get(i).score();

            List<Vector.VectorElement> compareReviewList =
                    users.get(similars.get(i).id()).getNormalizedRatings();

            compareReviewList.stream()
                    .filter(rating -> !alreadyRead.contains(rating.label()))
                    .forEach(rating -> {
                        double score = rating.value() * similarScore;
                        if (recommends.containsKey(rating.label())
                                && recommends.get(rating.label()) <= score) {
                            return;
                        }
                        recommends.put(rating.label(), score);
                    });

        }

        return recommends
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .limit(maxRecommend)
                .toList();
    }

}
