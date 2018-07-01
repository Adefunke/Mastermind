/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advancedga;

import java.util.Random;

/**
 * @author FAkinola
 * this handles computations such as mutation --allelle, gray coding etc
 */
public class Computations {
    public boolean isChromosomeinPopulation(ChromosomeSelection chromosome,
                                            Population population, int activeSize) {
        for (int i = 0; i < activeSize; i++) {
            if (chromosome == population.getChromosome(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param bound picks a random number with the range of the bound
     *              e.g if bound 2, the options are 0 or 1- binary
     *              if bound is 10, 0,1,2,3,4,5,6,7,8 or 9 -symbolic
     * @return a random number
     */
    private Integer aNumber(int bound) {
        return new Random().nextInt(bound);
    }

    /**
     * @param gene
     * @param bound
     * @return allelle; works for both symbolic and binary mutation
     */
    public Integer getRandomAllele(int gene, int bound) {
        int allele = aNumber(bound);
        if (allele == gene) {
            getRandomAllele(gene, bound);
        }else{
            return allele;
        }
        return getRandomAllele(gene,bound);
    }

}
