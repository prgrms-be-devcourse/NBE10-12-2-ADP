package com.back.standard.recommend.util;

import java.util.Comparator;
import java.util.List;

import static com.back.standard.recommend.util.Vector.VectorElement;

public class CosineSimilarityCalcer implements SimilarityCalcer {

    private double vectorASqrMagnitude;
    private double vectorBSqrMagnitude;

    private List<VectorElement> vectorA;
    private List<VectorElement> vectorB;

    private static List<VectorElement> getVector(List<VectorElement> vector) {
        return vector
                .stream()
                .sorted(Comparator
                        .comparingLong(
                                VectorElement::label))
                .toList();
    }

    private static double getSqrMagnitude(List<VectorElement> ratings) {

        return ratings
                .stream()
                .mapToDouble(
                vectorElement -> vectorElement.value() * vectorElement.value())
                .sum();
    }

    public void setVectorA(List<VectorElement> vector) {
        vectorA = getVector(vector);
        vectorASqrMagnitude = getSqrMagnitude(vector);
    }

    public void setVectorB(List<VectorElement> vector) {
        vectorB = getVector(vector);
        vectorBSqrMagnitude = getSqrMagnitude(vector);
    }

    public double getCosineSimilarity() {

        int refA = 0;
        int refB = 0;

        double ret = 0;

        while (refA < vectorA.size() && refB < vectorB.size()) {
            VectorElement ratingA = vectorA.get(refA);
            VectorElement ratingB = vectorB.get(refB);

            if (ratingA.label() < ratingB.label()) {
                refA++;
                continue;
            }
            if (ratingA.label() > ratingB.label()) {
                refB++;
                continue;
            }

            ret += ratingA.value() * ratingB.value();

            refA++;
            refB++;
        }

        return ret / Math.sqrt(vectorASqrMagnitude * vectorBSqrMagnitude);
    }

}
