package com.back.standard.recommend.byReview;

import java.util.stream.Stream;

public interface RecommendByReview {

    record RecommendReview (
            long reviewerId,
            long subjectId,
            float rating
    ) { }

    void setData(Stream<RecommendReview> reviews);
    Stream<Long> getRecommend(long targetUserId, int referenceCnt, int maxRecommend);

}
