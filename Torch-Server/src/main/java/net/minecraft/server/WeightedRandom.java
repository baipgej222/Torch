package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class WeightedRandom {

    public static int a(List<? extends WeightedRandom.WeightedRandomChoice> list) {
        int i = 0;
        int j = 0;

        for (int k = list.size(); j < k; ++j) {
            WeightedRandom.WeightedRandomChoice weightedrandom_weightedrandomchoice = (WeightedRandom.WeightedRandomChoice) list.get(j);

            i += weightedrandom_weightedrandomchoice.a;
        }

        return i;
    }

    public static WeightedRandom.WeightedRandomChoice a(Random random, List list, int i) {
        if (i <= 0) {
            throw new IllegalArgumentException();
        } else {
            int j = random.nextInt(i);

            return a(list, j);
        }
    }

    public static WeightedRandom.WeightedRandomChoice a(List list, int i) {
        int j = 0;

        for (int k = list.size(); j < k; ++j) {
            WeightedRandom.WeightedRandomChoice weightedrandom_weightedrandomchoice = (WeightedRandom.WeightedRandomChoice) list.get(j);

            i -= weightedrandom_weightedrandomchoice.a;
            if (i < 0) {
                return weightedrandom_weightedrandomchoice;
            }
        }

        return null;
    }

    public static WeightedRandom.WeightedRandomChoice a(Random random, List list) {
        return a(random, list, a(list));
    }

    public static class WeightedRandomChoice {

        protected int a;

        public WeightedRandomChoice(int i) {
            this.a = i;
        }
    }
}
