package com.back.standard.recommend.byRating;

import com.back.standard.recommend.util.CosineSimilarityCalcer;
import com.back.standard.recommend.util.SimilarityCalcer;
import com.back.standard.recommend.util.Vector;

import java.util.*;
import static com.back.standard.recommend.util.Vector.VectorElement;

public class SimilarityRecommendByRating {

    public record RecommendReview (
            long reviewerId,
            long subjectId,
            float rating
    ) { }

    record Similar (
            long id,
            double score
    ) { }

    private final Map<Long, Vector> users;
    private SimilarityCalcer calcer = new CosineSimilarityCalcer();

    public SimilarityRecommendByRating() {
        users = new HashMap<>();
    }

    public SimilarityRecommendByRating(SimilarityCalcer calcer) {
        users = new HashMap<>();
        this.calcer = calcer;
    }

    public void clear() {
        users.clear();
    }

    public void setData(List<RecommendReview> reviews) {

        reviews.forEach(review -> {
            if (!users.containsKey(review.reviewerId())) {
                users.put(review.reviewerId(), new Vector());
            }
            users.get(review.reviewerId()).putValue(
                    review.subjectId(), review.rating());

        });

    }

    private List<Similar> getSimilarList(Vector target) {

        users.replaceAll((_, oldValue) ->
                oldValue.subtractionValue(oldValue.getAverageValue()));

        List<Similar> similarList = new ArrayList<>();

        calcer.setVectorA(target);

        users.entrySet().stream()
                .filter(set -> set.getValue() != target)
                .filter(set -> !set.getValue().isEmpty())
                .forEach(set -> {
                    calcer.setVectorB(set.getValue());
                    similarList.add(new Similar(set.getKey(), calcer.getCosineSimilarity()));
                });

        return similarList;
    }

    public List<Long> getRecommendList(long targetUserId, int referenceCnt, int maxRecommend) {

        Vector targetUser = users.getOrDefault(targetUserId, null);

        if (targetUser == null) return List.of();
        if (targetUser.isEmpty()) return List.of();

        List<Similar> similarList = getSimilarList(targetUser);
        similarList.sort(Comparator.comparingDouble(a -> -a.score()));

        Map<Long, Double> recommends = new HashMap<>();
        Set<Long> alreadyRead = targetUser.getLabels();

        for (int i = 0; i < referenceCnt && i < similarList.size(); i++) {

            double similarScore = similarList.get(i).score();

            List<VectorElement> compareReviewList =
                    users.get(similarList.get(i).id()).getVectorElementList();

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
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .limit(maxRecommend)
                .toList();
    }

}
