/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advancedga;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author FAkinola
 */
public class AdvancedGA {

    public ChromosomeSelection[][] chromosomeSelected;
    /**
     * @param args the command line arguments
     */
    private Population population = new Population();
    private Population guessPopulation = new Population();
    private Population tempPopulation;
    private Population switchOverPopulation = population;
    private Computations computations = new Computations();
    private ChromosomeSelection fittest;
    private ChromosomeSelection secondFittest;
    private ChromosomeSelection firstPicked;
    private ChromosomeSelection secondPicked;
    public ChromosomeSelection answer;
    private int generationCount = 1;
    private boolean fps = true;
    private double[] fitnessProb;
    double currentHighestlevelOfFitness;
    int noOfmutations = 0;
    int noOfSwapmutations = 0;
    int noOfTwoPointmutations = 0;
    int noOfComputatons = 0;
    int noOfCrossover = 0;
    //this controls if what we are computing contains integer or binary values
    private int bound = 6;
    int popSize = 150;
    //this dictates the length of each individuals/chromosomes
    private int geneLength = 4;

    public static void main(String[] args) {

        Random rn = new Random();
        String fittestChromosome = "";
        AdvancedGA ga = new AdvancedGA();

        ga.answer = new ChromosomeSelection(ga.bound, ga.geneLength);
        //Initialize population
        ga.guessPopulation.initializeGuessPopulation(ga.popSize);
        ga.answer.setColors();
        String answer = ga.answer.getChromosome(ga.answer.colors);
        String correctnessOfGuess = "";
        ga.bound = ga.guessSetup(correctnessOfGuess, answer);
        ga.population.initializePopulation(ga.bound, ga.geneLength, ga.popSize);

        //Calculate fitness of each chromosome to get the fittest before evolution begins
        ga.fittest = ga.population.calculateFitness(ga.guessPopulation, ga.generationCount);
        ga.currentHighestlevelOfFitness = ga.population.fittest;
        System.out.println("Generation: " + ga.generationCount + " Fittest: " + ga.currentHighestlevelOfFitness);
        fittestChromosome = ga.fittest.getChromosome(ga.answer.colors);
        System.out.println(fittestChromosome);
        //(ga.geneLength * (ga.geneLength + 1) / 2)
        //While population searches for a chromosome with maximum fitness
        while (!(ga.fittest.getChromosome(ga.answer.colors).equalsIgnoreCase(ga.answer.getChromosome(ga.answer.colors)))
                && ga.generationCount < ga.popSize) {
            ++ga.generationCount;
            ga.tempPopulation = ga.population;
            if (ga.fps) {
                ga.fitnessProb = ga.tempPopulation.calculateProbFitness(ga.guessPopulation, ga.generationCount);
            }
            int beginfrom = ga.naturalSelection(true);
            //Do the things involved in evolution
            for (; beginfrom < ga.popSize; beginfrom += 2) {
                if (ga.fps) {
                    ga.fPSelection();
                } else {
                    ga.tournamentSelection(ga.tempPopulation.populationSize);
                }
                ++ga.noOfComputatons;
                //crossover with a random and quite high probability
//                if (rn.nextInt() % 5 < 4) {
//                    ++ga.noOfCrossover;
                ga.twoPointCrossover();
                //}

                //mutate with a random and quite low probability
                if (rn.nextInt() % 16 > 14) {
                    ++ga.noOfmutations;
                    ga.mutation();
                }
                if (rn.nextInt() % 16 > 14) {
                    ++ga.noOfSwapmutations;
                    ga.swapMutation();
                }
                if (rn.nextInt() % 23 >= 22) {
                    ++ga.noOfTwoPointmutations;
                    ga.TwoPointInversionMutation();
                }
                ga.randomizedMutation(beginfrom);
            }
            // moving the new generation into the old generation space
            ga.population = ga.switchOverPopulation;

            //Calculate new fitness value
            ga.fittest = ga.population.calculateFitness(ga.guessPopulation, ga.generationCount);
            ga.currentHighestlevelOfFitness = ga.population.fittest;
            System.out.println("Generation: " + ga.generationCount + " Fittest: " + ga.currentHighestlevelOfFitness);
            fittestChromosome = ga.fittest.getChromosome(ga.answer.colors);
            ga.guessPopulation.saveChromosomes(ga.generationCount + ga.bound - 2, ga.fittest);
            correctnessOfGuess = ga.fittest.calcGuessFitness(ga.answer);
            ga.guessPopulation.xEs[ga.generationCount + ga.bound - 2] = Integer.parseInt(correctnessOfGuess.
                    substring(0, correctnessOfGuess.indexOf(",")));
            ga.guessPopulation.yEs[ga.generationCount + ga.bound - 2] = Integer.parseInt(correctnessOfGuess.
                    substring(correctnessOfGuess.indexOf(",") + 1));

            System.out.println(fittestChromosome);
        }
        //when a solution is found or 100 generations have been produced
        if (fittestChromosome.equalsIgnoreCase(ga.answer.getChromosome(ga.answer.colors))) {
            System.out.println("\nYaay!!!\nI've found the answer" +
                    "\nMy solution was found in generation " + ga.generationCount);
        } else {
            System.out.println("\nEvolution stopped in generation " + ga.generationCount);
        }
        System.out.println("Fitness: " + ga.currentHighestlevelOfFitness);
        System.out.print("Genes: ");
        System.out.println(fittestChromosome);
        System.out.println("probability of mutation is " + (double) ga.noOfmutations / ga.noOfComputatons);
        System.out.println("probability of SwapMutation is " + (double) ga.noOfSwapmutations / ga.noOfComputatons);
        System.out.println("probability of TwoPointMutation is " + (double) ga.noOfTwoPointmutations / ga.noOfComputatons);
        System.out.println("probability of cross over is " + 1);
        System.out.println("The actual value to be guessed was " + ga.answer.getChromosome(ga.answer.colors));
    }

    //Selection
    int naturalSelection(boolean elitism) {
        if (elitism) {
            //Select the most fittest chromosome
            fittest = tempPopulation.getFittest();

            //Select the second most fittest chromosome
            secondFittest = tempPopulation.getSecondFittest();

            switchOverPopulation.saveChromosomes(0, fittest);
            switchOverPopulation.saveChromosomes(1, secondFittest);
            return 2;
        }
        return 0;
    }

    /**
     * @param correctnessOfGuess
     * @param answerString
     * @return rearrange the bound and colors with respect to the answer
     * So the 6 original guesses reduce the size of colors and this in turn is used to set the location of the answer
     */
    int guessSetup(String correctnessOfGuess, String answerString) {
        for (int i = 0; i < bound; i++) {
            correctnessOfGuess = guessPopulation.chromosomes[i].calcGuessFitness(answer);
            if (Integer.parseInt(correctnessOfGuess.
                    substring(0, correctnessOfGuess.indexOf(","))) != 0) {
                guessPopulation.xEs[i] = Integer.parseInt(correctnessOfGuess.
                        substring(0, correctnessOfGuess.indexOf(",")));
                guessPopulation.yEs[i] = Integer.parseInt(correctnessOfGuess.
                        substring(correctnessOfGuess.indexOf(",") + 1));
            } else {
                answer.colors.remove(i);
                for (int a = i; a < bound; a++) {
                    guessPopulation.chromosomes[a] = guessPopulation.chromosomes[a + 1];
                }
                --bound;
                --i;
            }
        }
        for (int i = 0; i < bound; i++) {
            if (guessPopulation.chromosomes[i].getGene(0) != i) {
                for (int a = 0; a < 4; a++) {
                    guessPopulation.chromosomes[i].setGene(0, i);
                }
            }
        }
        String[] myList = answerString.split(" ");
        for (int i = 0; i < 4; i++) {
            answer.setGene(i, answer.colors.indexOf(myList[i] + " "));

        }
        return bound;
    }

    /**
     * @param popSize this picks two chromosomes randomly. In tournament selection, the norm is to randomly pick k numbers of chromosomes,
     *                then select the best and return it to the population so as to increase the chance of picking global optimum.
     *                k can be between 1 and n; Here, I'm picking one random chromosome each then the reproduction process.
     */
    private void tournamentSelection(int popSize) {
        firstPicked = tempPopulation.randomlyPicked(popSize);
        secondPicked = tempPopulation.randomlyPicked(popSize);

    }

    /**
     * @param popsiz tournament selection where 2 chromosomes are randomly picked and the fittest is added again to the population;
     *               the population size would ideally increase by 2 but it would give more options.
     */
    private void tournamentSelections(int popsiz) {
        Population tournamentPopulation = new Population();
        popSize = popsiz;
        tournamentPopulation.initializePopulation(bound, geneLength, (2 * popSize));
        for (int i = 0; i < popsiz; i++) {
            tournamentPopulation.saveChromosomes(i, tempPopulation.getChromosome(i));
        }
        for (int i = 0; i < popsiz; i++) {
            tournamentSelection(popsiz);
            popSize++;
            if (firstPicked.fitness > secondPicked.fitness) {
                tournamentPopulation.saveChromosomes(popSize - 1, firstPicked);
            } else {
                tournamentPopulation.saveChromosomes(popSize - 1, secondPicked);
            }
        }
        firstPicked = tournamentPopulation.randomlyPicked(popSize);
        secondPicked = tournamentPopulation.randomlyPicked(popSize);
        popSize = 150;
    }

    private void fPSelection() {
        double rand = new Random().nextDouble();
        double rand2 = new Random().nextDouble();
        firstPicked = tempPopulation.getChromosome(positionOfChromosome(rand));
        secondPicked = tempPopulation.getChromosome(positionOfChromosome(rand2));
    }

    private int positionOfChromosome(double rand) {
        if (rand > 0.6) {
            for (int i = popSize - 1; i > 0; i--) {
                if (rand > fitnessProb[i]) {
                    return i + 1;
                }
            }

        } else {
            for (int i = 0; i < popSize; i++) {
                if (rand < fitnessProb[i]) {
                    return i;
                }
            }
        }
        return 0;
    }

    //Two point crossover
    private void twoPointCrossover() {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint1 = rn.nextInt(population.chromosomes[0].geneLength);
        int crossOverPoint2 = rn.nextInt(population.chromosomes[0].geneLength);
        if (crossOverPoint1 > crossOverPoint2) {
            int temp = crossOverPoint2;
            crossOverPoint2 = crossOverPoint1;
            crossOverPoint1 = temp;
        }
        //Swap values among parents
        for (int i = crossOverPoint1; i < crossOverPoint2; i++) {
            int temp = firstPicked.genes[i];
            firstPicked.genes[i] = secondPicked.genes[i];
            secondPicked.genes[i] = temp;

        }

    }

    //One point crossover
    private void onePointCrossover() {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint = rn.nextInt(population.chromosomes[0].geneLength);

        //Swap values among parents
        for (int i = 0; i < crossOverPoint; i++) {
            int temp = firstPicked.genes[i];
            firstPicked.genes[i] = secondPicked.genes[i];
            secondPicked.genes[i] = temp;

        }

    }

    //Uniform crossover
    private void uniformCrossover() {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint = rn.nextInt(population.chromosomes[0].geneLength);

        //Swap values uniformly among parents
        for (int i = 0; i < population.chromosomes[0].geneLength; i++) {
            int temp = firstPicked.genes[i];
            firstPicked.genes[i] = secondPicked.genes[i];
            secondPicked.genes[i] = temp;
            i++;
        }

    }

    /**
     * picking a random gene and swapping it with its allelle
     */
    private void mutation() {
        Random rn = new Random();

        //Select a random mutation point
        int mutationPoint = rn.nextInt(population.chromosomes[0].geneLength);

        //Flip values at the mutation point
        firstPicked.genes[mutationPoint] = computations.getRandomAllele(firstPicked.genes[mutationPoint], bound);

        mutationPoint = rn.nextInt(population.chromosomes[0].geneLength);
        secondPicked.genes[mutationPoint] = computations.getRandomAllele(secondPicked.genes[mutationPoint], bound);

    }

    private void swapMutation() {
        Random rn = new Random();

        //Select a random mutation point
        int mutationPoint = rn.nextInt(population.chromosomes[0].geneLength);
        int mutationPoint2 = rn.nextInt(population.chromosomes[0].geneLength);
        //Flip values at the mutation point
        int temp = firstPicked.getGene(mutationPoint);
        firstPicked.setGene(mutationPoint, firstPicked.getGene(mutationPoint2));
        firstPicked.setGene(mutationPoint2, temp);

        mutationPoint = rn.nextInt(population.chromosomes[0].geneLength);
        mutationPoint2 = rn.nextInt(population.chromosomes[0].geneLength);
        temp = secondPicked.getGene(mutationPoint);
        secondPicked.setGene(mutationPoint, secondPicked.getGene(mutationPoint2));
        secondPicked.setGene(mutationPoint2, temp);

    }

    private void TwoPointInversionMutation() {
        Random rn = new Random();

        //Select a random mutation point
        int mutationPoint = rn.nextInt(population.chromosomes[0].geneLength);
        int mutationPoint2 = rn.nextInt(population.chromosomes[0].geneLength);
        if (mutationPoint == mutationPoint2) {
            if (mutationPoint < population.chromosomes[0].geneLength - 3) {
                mutationPoint2 += (rn.nextInt(population.chromosomes[0].geneLength - mutationPoint - 2) + 1);
            } else {
                mutationPoint2 -= (rn.nextInt(mutationPoint) + 1);
            }
        }
        firstPicked = twoPointInversionDeterminant(mutationPoint, mutationPoint2, firstPicked);

        mutationPoint = rn.nextInt(population.chromosomes[0].geneLength);
        mutationPoint2 = rn.nextInt(population.chromosomes[0].geneLength);
        if (mutationPoint == mutationPoint2) {
            if (mutationPoint < population.chromosomes[0].geneLength - 2) {
                mutationPoint2 += (rn.nextInt(population.chromosomes[0].geneLength - mutationPoint - 2) + 1);
            } else {
                mutationPoint2 -= (rn.nextInt(mutationPoint) + 1);
            }
        }
        secondPicked = twoPointInversionDeterminant(mutationPoint, mutationPoint2, secondPicked);
    }

    private ChromosomeSelection twoPointInversionDeterminant(int mutationPoint,
                                                             int mutationPoint2,
                                                             ChromosomeSelection chromosome) {
        int[] tempArray;
        if (mutationPoint2 > mutationPoint) {
            tempArray = new int[mutationPoint2 - mutationPoint + 1];
            for (int i = mutationPoint; i < mutationPoint2 + 1; i++) {
                tempArray[i - mutationPoint] = chromosome.getGene(i);
            }
            for (int i = mutationPoint; i < mutationPoint2 + 1; i++) {
                chromosome.setGene(i, tempArray[tempArray.length - i + mutationPoint - 1]);
            }
        } else {
            tempArray = new int[chromosome.geneLength - mutationPoint +
                    mutationPoint2 + 1];
            for (int i = 0; i < tempArray.length; i++) {
                if (i + mutationPoint < tempArray.length
                        || tempArray.length < 4 && i + mutationPoint <= tempArray.length
                        || mutationPoint == 3 && i == 0) {
                    tempArray[i] = chromosome.getGene(i + mutationPoint);
                } else {
                    tempArray[i] = chromosome.getGene(mutationPoint + i - 4);
                }
            }
            for (int i = 0; i < tempArray.length; i++) {
                if (i + mutationPoint < tempArray.length
                        || tempArray.length < 4 && i + mutationPoint <= tempArray.length
                        || mutationPoint == 3 && i == 0) {
                    chromosome.setGene(i + mutationPoint,
                            tempArray[tempArray.length - i - 1]);
                } else {
                    chromosome.setGene(mutationPoint + i - 4,
                            tempArray[tempArray.length - i - 1]);
                }
            }
        }
        return chromosome;
    }

    private void randomizedMutation(int beginfrom) {
        if (computations.isChromosomeinPopulation(firstPicked, switchOverPopulation, beginfrom - 1)) {
            firstPicked = new ChromosomeSelection(bound, geneLength);
        }
        switchOverPopulation.saveChromosomes(beginfrom, firstPicked);

        if (computations.isChromosomeinPopulation(secondPicked, switchOverPopulation, beginfrom)) {
            secondPicked = new ChromosomeSelection(bound, geneLength);
        }
        switchOverPopulation.saveChromosomes(beginfrom + 1, secondPicked);

    }

}
