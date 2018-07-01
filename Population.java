/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advancedga;

import java.util.Random;

/**
 * @author FAkinola
 */
public class Population {
    ChromosomeSelection[] chromosomes;
    double[] fitnessProb;
    double fittest = 0;
    int maxFit;
    int populationSize = 0;
    int[] xEs = new int[150];
    int[] yEs = new int[150];

    //Initialize population
    public void initializePopulation(int bound, int geneLength, int popSize) {
        fitnessProb = new double[popSize];
        chromosomes = new ChromosomeSelection[popSize];
        populationSize = popSize;
        for (int i = 0; i < chromosomes.length; i++) {
            chromosomes[i] = new ChromosomeSelection(bound, geneLength);
        }
    }

    //Initialize guessPopulation

    public void initializeGuessPopulation(int popSize) {
        chromosomes = new ChromosomeSelection[popSize];
        for (int i = 0; i < chromosomes.length; i++) {
            if (i < 6) {
                chromosomes[i] = new ChromosomeSelection(i);
            }
        }
    }

    /**
     * @param index
     * @param chromosome saves a chromosome that has probably undergone change or is new
     */
    public void saveChromosomes(int index, ChromosomeSelection chromosome) {
        chromosomes[index] = chromosome;
    }

    /**
     * @param popSize
     * @return randomly pick within the array
     */
    public ChromosomeSelection randomlyPicked(int popSize) {
        return chromosomes[new Random().nextInt(popSize)];
    }

    public ChromosomeSelection getChromosome(int index) {
        return chromosomes[index];
    }

    /**
     * @return fittest chromosome
     */
    public ChromosomeSelection getFittest() {
        maxFit = 0;
        for (int i = 0; i < chromosomes.length; i++) {
            if (chromosomes[maxFit].fitness >= chromosomes[i].fitness) {
                maxFit = i;
            }
        }

        fittest = chromosomes[maxFit].fitness;
        return chromosomes[maxFit];
    }

    /**
     * @return second fittest chromosome when requested for via elitism
     */
    public ChromosomeSelection getSecondFittest() {
        int maxFit2 = 0;
        for (int i = 0; i < chromosomes.length; i++) {
            if (chromosomes[maxFit2].fitness > chromosomes[i].fitness && i != maxFit) {
                maxFit2 = i;
            }
        }
        return chromosomes[maxFit2];
    }

    public ChromosomeSelection calculateFitness(Population guessPopulation, int generationCount) {

        for (int i = 0; i < chromosomes.length; i++) {
            chromosomes[i].calcFitness(guessPopulation, generationCount);

        }
        return getFittest();
    }

    /**
     * @return calculates the cumulative fitness of each member
     */
    public double calculateCumulativeFitness(Population guessPopulation, int generationCount) {
        double totalFitness = 0.0;
        for (int i = 0; i < chromosomes.length; i++) {
            totalFitness += chromosomes[i].calcFitness(guessPopulation, generationCount);
            fitnessProb[i] = totalFitness;

        }
        return totalFitness;
    }

    /**
     * @return calculates the cdf's probability
     */
    public double[] calculateProbFitness(Population guessPopulation, int generationCount) {
        double totalFitness = calculateCumulativeFitness(guessPopulation, generationCount);
        for (int i = 0; i < chromosomes.length; i++) {
            fitnessProb[i] = fitnessProb[i] / totalFitness;

        }
        return fitnessProb;
    }

}
