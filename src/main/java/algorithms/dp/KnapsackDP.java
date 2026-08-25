package algorithms.dp;

/**
 * Owner: Thelma Osei-Fiagbor
 * Implementation of 0/1 Knapsack Dynamic Programming for waste collection service request selection.
 * Optimizes total priority / urgency value under a truck weight/volume capacity budget.
 */
public class KnapsackDP {

    public static class RequestItem {
        public String requestId;
        public int weightKg;
        public int priorityValue;

        public RequestItem(String requestId, int weightKg, int priorityValue) {
            this.requestId = requestId;
            this.weightKg = weightKg;
            this.priorityValue = priorityValue;
        }
    }

    public static class DPResult {
        public int maxPriority;
        public RequestItem[] selectedItems;
        public int[][] dpTable;

        public DPResult(int maxPriority, RequestItem[] selectedItems, int[][] dpTable) {
            this.maxPriority = maxPriority;
            this.selectedItems = selectedItems;
            this.dpTable = dpTable;
        }

        public String getTableFormatted() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== 0/1 Knapsack DP Tabulation Table ===\n");
            int rows = dpTable.length;
            int cols = dpTable[0].length;

            sb.append(String.format("%-10s", "Item/Cap"));
            for (int w = 0; w < cols; w++) {
                sb.append(String.format("%-6d", w));
            }
            sb.append("\n");

            for (int i = 0; i < rows; i++) {
                sb.append(String.format("%-10s", "Item " + i));
                for (int w = 0; w < cols; w++) {
                    sb.append(String.format("%-6d", dpTable[i][w]));
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Solves 0/1 Knapsack request selection problem using Tabulation.
     */
    public static DPResult solveKnapsack(RequestItem[] items, int capacityKg) {
        if (items == null || capacityKg < 0) {
            throw new IllegalArgumentException("Invalid items array or capacity");
        }

        int n = items.length;
        int[][] dp = new int[n + 1][capacityKg + 1];

        // Build DP table in bottom-up manner
        for (int i = 1; i <= n; i++) {
            int wt = items[i - 1].weightKg;
            int val = items[i - 1].priorityValue;
            for (int w = 0; w <= capacityKg; w++) {
                if (wt <= w) {
                    dp[i][w] = Math.max(val + dp[i - 1][w - wt], dp[i - 1][w]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Backtrack to find selected items
        int res = dp[n][capacityKg];
        int w = capacityKg;

        RequestItem[] tempSelected = new RequestItem[n];
        int count = 0;

        for (int i = n; i > 0 && res > 0; i--) {
            if (res != dp[i - 1][w]) {
                tempSelected[count++] = items[i - 1];
                res -= items[i - 1].priorityValue;
                w -= items[i - 1].weightKg;
            }
        }

        RequestItem[] selected = new RequestItem[count];
        for (int i = 0; i < count; i++) {
            selected[i] = tempSelected[count - 1 - i]; // preserve order
        }

        return new DPResult(dp[n][capacityKg], selected, dp);
    }
}
