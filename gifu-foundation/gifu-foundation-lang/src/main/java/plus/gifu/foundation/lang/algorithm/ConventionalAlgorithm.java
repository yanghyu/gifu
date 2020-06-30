package plus.gifu.foundation.lang.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 常规算法
 *
 * @author yanghongyu
 * @since 2020-06-26
 */
public class ConventionalAlgorithm {

    /**
     * 给定数组（数字可重复），求两数和为指定值的所有下标
     *
     * @param arr       数组
     * @param expectSum 指定值
     */
    public static void expectSumBySet(int[] arr, int expectSum) {
        if(arr == null || arr.length == 0)
            return;
        HashSet<Integer> intSets = new HashSet<Integer>(arr.length);

        int suplement;
        for (int i : arr)
        {
            suplement = expectSum - i;
            if(!intSets.contains(suplement)){
                intSets.add(i);
            }else{
                System.out.println(i + " + " + suplement + " = " + expectSum);
            }
        }
    }


    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        throw new IllegalArgumentException("No two sum solution");
    }

    public static void main(String[] args) {
        int[] arr = {2,7,4,9,3};
        int expectSum = 11;
        expectSumBySet(arr, expectSum);
        int[] arr2 = {3,7,9,1,2,8,5,6,10,5};
        int expectSum2 = 10;
        expectSumBySet(arr2, expectSum2);
    }

}
