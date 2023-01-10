package com.example.weight_inspection.services;

import org.springframework.stereotype.Service;

@Service
public class WeighingService implements WeighingServiceInterface {


    @Override
    public float calculateWeightOfOneProduct(float totalWeight, int numberOfProducts, int quantityOfProductsInPackaging,
                                             float packagingWeight, float paletteWeight) {
        int numberOfPackages = (int) Math.ceil((float)numberOfProducts / quantityOfProductsInPackaging);
        float totalWeightOfPackagingAndPalette = (numberOfPackages * packagingWeight) + paletteWeight;
        float productWeight = totalWeight - totalWeightOfPackagingAndPalette;
        return productWeight / numberOfProducts;
    }

    @Override
    public float calculateExpectedWeight(int numberOfProducts, float weightOfOneProduct,
                                         float packagingWeight, float paletteWeight) {
        float weightOfProducts = numberOfProducts * weightOfOneProduct;
        return weightOfProducts + packagingWeight + paletteWeight;

    }
}
