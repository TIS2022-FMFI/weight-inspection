package com.example.weight_inspection.services;

public interface WeighingServiceInterface {
    float calculateWeightOfOneProduct(float totalWeight,int numberOfProducts, int quantityOfProductsInPackaging,
                                      float packagingWeight, float paletteWeight);

    float calculateExpectedWeight(int numberOfProducts,  float weightOfOneProduct,
                                  float packagingWeight, float paletteWeight);
}
