/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advancedga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author FAkinola
 */
public class ChromosomeSelection {
    double fitness;
    int geneLength = 4;
    int[] genes = new int[geneLength];
    int boundd;
    ArrayList<String> colors = new ArrayList(6);

    public ChromosomeSelection(int bound, int geneLength) {
        Random rn = new Random();
        boundd = bound;
        genes = new int[geneLength];
        //Set genes randomly for each chromosome

        for (int i = 0; i < genes.length; i++) {
            genes[i] = rn.nextInt(bound);
        }

        fitness = 0;
    }

    public ChromosomeSelection(int point) {
        for (int i = 0; i < genes.length; i++) {
            genes[i] = point;
        }
    }

    /**
     * @return converts the genes in a chromosome to a string
     */
    public String getChromosome(ArrayList res) {
        String chromosome = "";

        for (int i = 0; i < geneLength; i++) {
            colors = res;
            chromosome += color(getGene(i), res);
        }
        return chromosome;
    }

    public String getColor(ArrayList col, int index) {
        return (String) col.get(index);
    }

    public void setColors() {
        colors.add("Red ");
        colors.add("Orange ");
        colors.add("Yellow ");
        colors.add("Green ");
        colors.add("Blue ");
        colors.add("Indigo ");

    }

    public String color(int num, ArrayList res) {
        return getColor(res, num);
    }

    public String calcGuessFitness(ChromosomeSelection answer) {
        int xs = 0;
        int ys = 0;
        int[] blackHoles = new int[4];
        int j = 0;
        for (int i = 0; i < geneLength; i++) {
            if (genes[i] == answer.getGene(i)) {
                ++xs;
                blackHoles[j] = i;
                ++j;
                //fitness= (geneLength - i) + fitness;
            }
        }
        if (xs != geneLength) {
            for (int i = 0; i < geneLength; i++) {
                boolean blackHole = false;
                for (int a = 0; a < xs; a++) {
                    if (i == blackHoles[a]) {
                        blackHole = true;
                    }
                }
                if (!blackHole) {

                    for (int y = 0; y < geneLength; y++) {
                        if (genes[i] == answer.getGene(y)) {
                            ++ys;
                        }
                    }
                }
            }
        }
        return xs + "," + ys;
    }

    public double calcFitness(Population guessPopulation, int generationCount) {
        int xs = 0;
        int ys = 0;
        fitness = 0;
        String correctness = "";
        for (int i = 0; i < generationCount - 1; i++) {
            correctness = calcGuessFitness(guessPopulation.getChromosome(i));
            xs += Math.abs(guessPopulation.xEs[i] - Integer.parseInt(correctness.
                    substring(0, correctness.indexOf(","))));

            ys += Math.abs(guessPopulation.yEs[i] - Integer.parseInt(correctness.
                    substring(correctness.indexOf(",") + 1)));

        }
        fitness = (2 * xs) + ys;
        return fitness;
    }

    public int getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, int value) {
        genes[index] = value;
    }

}
