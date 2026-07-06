package com.back.standard.recommend.util;

public interface SimilarityCalcer {

    void setVectorA(Vector vector);
    void setVectorB(Vector vector);
    double getCosineSimilarity();

}
