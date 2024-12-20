import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {
    private int populationSize;
    private int generations;
    private double mutationRate;
    private Random gen; // Random number generator
    static final int FITNESS_THRESHOLD = 100; // Contoh nilai optimal fitness

    public GeneticAlgorithm(int populationSize, int generations, double mutationRate, Random random) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.gen = random; // Menggunakan Random yang diberikan
    }


    // Metode utama untuk menyelesaikan puzzle
    public Individual solve(Puzzle puzzle) {
        ArrayList<Individual> population = initializePopulation(puzzle);

        for (int genCount = 0; genCount < generations; genCount++) {
            // Evolusi populasi
            population = evolve(population, puzzle);

            // Cari individu terbaik
            Individual best = findBestIndividual(population);
            //System.out.println("Generasi " + genCount + " - Fitness: " + best.getFitness());

            // Jika solusi ditemukan
            if (best.getFitness() >= FITNESS_THRESHOLD) {
                //System.out.println("Solusi ditemukan di generasi ke-" + genCount);
                return best;
            }
        }

        // Jika tidak ditemukan solusi optimal
        //System.out.println("Solusi tidak ditemukan setelah " + generations + " generasi.");
        return findBestIndividual(population);
    }

    // Inisialisasi populasi
    private ArrayList<Individual> initializePopulation(Puzzle puzzle) {
        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(new Individual(puzzle, gen));
        }
        return population;
    }

    // Evolusi populasi: seleksi, crossover, mutasi
    private ArrayList<Individual> evolve(ArrayList<Individual> population, Puzzle puzzle) {
        ArrayList<Individual> newPopulation = new ArrayList<>();

        while (newPopulation.size() < populationSize) {
            // Seleksi parent
            // Individual parent1 = selectParent(population);
            // Individual parent2 = selectParent(population);
            // pilih parent ranked berdasarkan 1/2 dari populasi
            Individual parent1 = selectParentRanked(population, populationSize / 2);
            Individual parent2 = selectParentRanked(population, populationSize / 2);

            // Crossover
            Individual child = parent1.crossover(parent2, gen);

            // Mutasi
            if (gen.nextDouble() < mutationRate) {
                child.mutate(gen);
            }

            newPopulation.add(child);
        }

        return newPopulation;
    }

    // Seleksi parent menggunakan Roulette Wheel Selection
    private Individual selectParent(ArrayList<Individual> population) {
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

    // cara lain untuk seleksi randomized ranks :
    private Individual selectParentRanked(ArrayList<Individual> population, int tournamentSize) {
        // tournamentSize ini untuk fine-tune pilihannya berapa, diisi pada parameter
        ArrayList<Individual> tournamentParticipants = new ArrayList<>();

        // pilih acak sebanyak tournament size
        for (int i = 0; i < tournamentSize; i++) {
            Individual participant = population.get(gen.nextInt(population.size()));
            tournamentParticipants.add(participant);
        }

        // cari individu terbaik dari arraylist individual yang dipilih
        Individual bestParent = tournamentParticipants.get(0);
        for (Individual participant : tournamentParticipants) {
            if (participant.getFitness() > bestParent.getFitness()) {
                bestParent = participant;
            }
        }

        return bestParent;
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
}
