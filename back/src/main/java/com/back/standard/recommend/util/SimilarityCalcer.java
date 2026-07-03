package com.back.standard.recommend.util;

import java.util.List;

public interface SimilarityCalcer {

    void setVectorA(List<Vector.VectorElement> vector);
    void setVectorB(List<Vector.VectorElement> vector);
    double getCosineSimilarity();

}
