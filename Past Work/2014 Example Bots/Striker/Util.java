package Striker;

import battlecode.common.Clock;

public class Util {
        
        static int m_z = Clock.getBytecodeNum();
        static int m_w = Clock.getRoundNum();

        /**
         * Sets up our RNG given two seeds.
         * @param seed1
         * @param seed2
         */
        public static void randInit(int seed1, int seed2) {
            m_z = seed1;
            m_w = seed2;
        }

        private static int gen() {
            m_z = 36969 * (m_z & 65535) + (m_z >> 16);
            m_w = 18000 * (m_w & 65535) + (m_w >> 16);
            return (m_z << 16) + m_w;
        }
        
        /**
         * Returns a random {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}
         * @return
         */
        public static int randInt() {
                return gen();
        }
        
        /**
         * Returns a double between 0 and 1.0
         * @return
         */
        public static double randDouble() {
                return (gen() * 2.32830644e-10 + 0.5);
        }
        
        /**
         * Calls randDouble()
         * @return
         */
        public static double Random() {
                return randDouble();
        }
}