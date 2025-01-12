import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {
    private int populationSize;
    private int generations;
    private double mutationRate;
    private Random gen; // Random number generator
    // nilai fitness minimum
    static final int FITNESS_THRESHOLD = 55;

    public GeneticAlgorithm(int populationSize, int generations, double mutationRate, Random random) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.gen = random;
    }

    // Add getter methods
    public int getPopulationSize() {
        return populationSize;
    }

    public int getGenerations() {
        return generations;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public Individual solve(Puzzle puzzle) {
        // buat list populasi
        ArrayList<Individual> population = initializePopulation(puzzle);
        Individual bestIndividual = findBestIndividual(population);
        // jumlah generasi yang tidak punya improvement / stuck
        int genLocalOptimum = 0;

        // loop ini berjalan sebanyak nilai gen dari params.txt
        for (int genCount = 0; genCount < generations; genCount++) {
            // evolusi dari populasi yg ada
            population = evolve(population, puzzle);
            // ambil individu terbaik di generasi saat ini
            Individual currentTopIndividual = findBestIndividual(population);
            // cek fitnessnya, compare dengan individual terbaik keseluruhan
            // jika lebih baik, override bestIndividual
            if (currentTopIndividual.getFitness() > bestIndividual.getFitness()) {
                bestIndividual = currentTopIndividual;
                // reset counter genLocalMaxima
                genLocalOptimum = 0;
            } else {
                genLocalOptimum++;
            }
            // jika stuck di local optimum, diversifikasikan lalu looping lagi
            if (genLocalOptimum > 50) {
                population = diversifyPopulation(population, puzzle);
                genLocalOptimum = 0;
            }
            if (bestIndividual.getFitness() >= FITNESS_THRESHOLD) {
                return bestIndividual;
            }
        }
        return bestIndividual;
    }

    // Inisialisasi populasi
    private ArrayList<Individual> initializePopulation(Puzzle puzzle) {
        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(new Individual(puzzle, gen));
        }
        return population;
    }

    // revisi method evolve
    private ArrayList<Individual> evolve(ArrayList<Individual> population, Puzzle puzzle) {
        ArrayList<Individual> newPopulation = new ArrayList<>();

        // ambil 10% individu/sampel terbaik
        int topIndividualCount = populationSize / 10;
        ArrayList<Individual> sortedPopulation = new ArrayList<>(population);
        // pakai lamda untuk sort/compare data supaya terurut berdasarkan fitnessnya
        sortedPopulation.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
        // masukkan ke newPopulation
        for (int i = 0; i < topIndividualCount; i++) {
            newPopulation.add(sortedPopulation.get(i));
        }
        // proses crossover
        while (newPopulation.size() < populationSize) {
            Individual parent1 = tournamentSelection(population, 5);
            Individual parent2 = tournamentSelection(population, 5);

            Individual child = parent1.crossover(parent2, gen);
            // mutasi selama dibawah mutationRate
            if (gen.nextDouble() < mutationRate) {
                child.mutate(gen);
            }
            newPopulation.add(child);
        }

        return newPopulation;
    }

    // seleksi parent menggunakan tournament selection
    private Individual tournamentSelection(ArrayList<Individual> population, int tournamentSize) {
        Individual best = null;

        // loop sebanyak tournamentSize
        for (int i = 0; i < tournamentSize; i++) {
            // memilih individu secara acak dari populasi
            Individual contestant = population.get(gen.nextInt(population.size()));

            // jika belum ada individu terbaik, atau individu terpilih memiliki fitness
            // lebih tinggi, maka update individu terbaik
            if (best == null || contestant.getFitness() > best.getFitness()) {
                best = contestant;
            }
        }
        return best;
    }

    // seleksi parent menggunakan Roulette Wheel Selection
    private Individual rouletteWheelSelection(ArrayList<Individual> population) {
        double totalFitness = population.stream().mapToDouble(Individual::getFitness).sum();
        double rouletteWheel = gen.nextDouble() * totalFitness;

        double cumulativeFitness = 0.0;
        for (Individual individual : population) {
            cumulativeFitness += individual.getFitness();
            if (cumulativeFitness >= rouletteWheel) {
                return individual;
            }
        }

        return population.get(0); // Default fallback
    }

    // Cari individu terbaik dalam populasi
    private Individual findBestIndividual(ArrayList<Individual> population) {
        Individual best = population.get(0);
        for (Individual individual : population) {
            if (individual.getFitness() > best.getFitness()) {
                best = individual;
            }
        }
        return best;
    }

    // ini untuk regenerate supaya sampelnya tidak itu-itu saja
    private ArrayList<Individual> diversifyPopulation(ArrayList<Individual> population, Puzzle puzzle) {
        // Keep top 20% and regenerate rest randomly
        int keepSize = populationSize / 5;
        ArrayList<Individual> newPopulation = new ArrayList<>();

        population.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
        for (int i = 0; i < keepSize; i++) {
            newPopulation.add(population.get(i));
        }

        while (newPopulation.size() < populationSize) {
            newPopulation.add(new Individual(puzzle, gen));
        }

        return newPopulation;
    }
}
