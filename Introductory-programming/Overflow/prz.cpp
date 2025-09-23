// Author: Mateusz Sawicki

#include <iostream>
#include <map>
#include <numeric>
#include <queue>
#include <vector>

static std::map <std::vector <int>, int> dp; // Minimal number of moves required to reach given state.
static std::vector <int> capacity, target;
static int n; // Number of cups

/*
For the target to be attainable:
-at least one cup's target has to be full or empty
-all of the targets have to be divisible by the gcd of the capacity of all cups.
*/
bool checkConditions() {
    if(n == 0) return 1;
    bool emp = 0, ful = 0;
    int totalGcd = capacity[0];
    for(int i = 0; i < n; i ++) {
        totalGcd = std::gcd(totalGcd, capacity[i]);
        if(target[i] == 0) emp = 1;
        if(target[i] == capacity[i]) ful = 1;
    }
    if(!emp && !ful) return 0;
    for(int i = 0; i < n; i ++) {
        if(target[i] % totalGcd != 0) return 0;
    }
    return 1;
}

/*
BFS through all possible states of cups.
Assumes the answer is at most INT_MAX.
*/
int Solve() {
    if(n == 0) return 0;
    std::queue <std::vector <int> > q;
    std::vector <int> start; // the starting state with all cups empty
    for(int i = 0; i < n; i ++) start.push_back(0);
    dp[start] = 0;
    q.push(start);
    while(q.size() > 0) {
        std::vector <int> curr = q.front(), next = q.front();
        if(curr == target) return dp[curr];
        q.pop();

        for(int i = 0; i < n; i ++) {
            for(int j = 0; j < n; j ++) {
                if(i == j) continue;
                // pour from cup i to j
                next[j] = std::min(capacity[j], curr[j] + curr[i]);
                next[i] = curr[i] - (next[j] - curr[j]);
                if(dp.find(next) == dp.end()) {
                    dp[next] = dp[curr] + 1;
                    q.push(next);
                }
                next[i] = curr[i];
                next[j] = curr[j];
            }
        }

        for(int i = 0; i < n; i ++) {
            // top off cup i
            next[i] = capacity[i];
            if(dp.find(next) == dp.end()) {
                dp[next] = dp[curr] + 1;
                q.push(next);
            }
            next[i] = curr[i];
        }

        for(int i = 0; i < n; i ++) {
            // empty cup i
            next[i] = 0;
            if(dp.find(next) == dp.end()) {
                dp[next] = dp[curr] + 1;
                q.push(next);
            }
            next[i] = curr[i];
        }
    }
    return -1;
}


int main() {
    std::cin >> n;
    capacity.resize(n);
    target.resize(n);
    for(int i = 0; i < n; i ++) std::cin >> capacity[i] >> target[i];
    // remove degenerated cups
    std::vector <int> helper1, helper2;
    for(int i = 0; i < n; i ++) {
        if((capacity[i] != 0) || (target[i] != 0)) {
            helper1.push_back(capacity[i]);
            helper2.push_back(target[i]);
        }
    }
    swap(helper1, capacity);
    swap(helper2, target);
    n = (int) capacity.size();
    if(!checkConditions()) std::cout << -1 << '\n';
    else std::cout << Solve() << '\n';
    return 0;
}
