package com.hyfd.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FlatMapTest {

	public static void main(String[] args) {
		int[] arr = {-1, 0, 1, 2, -1, -4};
		int[] arr1 = {3};
		System.out.print(threeSum(arr));
	}

	public static int reverse(int x) {
		boolean flag = false;
		if(x<0){
			x = -x;
			flag = true;
		}
		String str = x + "";
        char[] arr = str.toCharArray();
        char[] newArr = new char[arr.length];
        for(int i = 0 ; i < arr.length ; i++){
        	newArr[i] = arr[arr.length-1-i];
        }
        String newStr = new String(newArr);
        try{
        	int newInt = Integer.parseInt(newStr);
        	return flag ? -newInt : newInt;
        }catch(Exception e){
        	return 0;
        }
    }
	
	public static boolean isPalindrome(int x) {
		String str = x + "";
        char[] arr = str.toCharArray();
        for(int i = 0 ; i < arr.length/2 ; i ++){
        	if(arr[i] != arr[arr.length -1 -i]){
        		return false;
        	}
        }
		return true;
    }
	
	public static int romanToInt(String s) {
		int sum = 0;
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("IV", 4);
        map.put("IX", 9);
        map.put("XL", 40);
        map.put("XC", 90);
        map.put("CD", 400);
        map.put("CM", 900);
        Set<String> set = map.keySet();
        for(String x : set){
        	if(s.contains(x)){
        		sum += map.get(x);
        		s = s.replace(x, "");
        	}
        }
        char[] arr = s.toCharArray();
        for(int i = 0 ; i < arr.length; i++){
        	if("M".equals(arr[i]+"")){
        		sum += 1000;
        	}else if("D".equals(arr[i]+"")){
        		sum += 500;
        	}else if("C".equals(arr[i]+"")){
        		sum += 100;
        	}else if("L".equals(arr[i]+"")){
        		sum += 50;
        	}else if("X".equals(arr[i]+"")){
        		sum += 10;
        	}else if("V".equals(arr[i]+"")){
        		sum += 5;
        	}else if("I".equals(arr[i]+"")){
        		sum += 1;
        	}
        }
		return sum;
    }
	
	public String longestCommonPrefix(String[] strs) {
		if(strs.length == 0){
			return "";
		}
		String s = "";
		char[] x = strs[0].toCharArray();
		for(int n = 0 ; n < x.length ; n++){
			s = s + x[n];
			for(int i = 1 ; i < strs.length; i++){
				if(strs[i].indexOf(s) != 0){
					return s.equals("")? "" : s.substring(0, s.length()-1);
				}
	        }
		}
		return strs[0];
    }
	
	public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode l = new ListNode(0);
        while(l1 != null && l2 != null){
        	if(l1.val < l2.val){
        		l.next = l1;
        		l = l.next;
        		l1 = l1.next;
        	}else {
        		l.next = l2;
        		l = l.next;
        		l2 = l2.next;
        	}
        }
        if(l1 == null){
        	l.next = l1;
        }
        if(l2 == null){
        	l.next = l2;
        }
		return l.next;
    }
	
	public static int removeDuplicates(int[] nums) {
		int i = 0;
        for (int n : nums)
            if (i < 2 || n > nums[i-2])
                nums[i++] = n;
        return i;
    }
	
	public int searchInsert(int[] nums, int target) {
		int x = 0;
        for(int i = 0 ; i < nums.length; i++){
        	if(nums[i] == target){
        		return i;
        	}
        	if(target > nums[i]){
        		x = i-1;
        	}
        }
		return x;
    }
	
	public static int removeElement(int[] nums, int val) {
		int n = 0;
		for(int i = 0 ; i < nums.length ; i++){
			if(nums[i] != val){
				nums[n++] = nums[i];
			}
		}
        return n-1;
    }
	
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if(nums2.length == 0){
        	if(nums1.length % 2 == 0){
        		return (nums1[nums1.length/2-1] + nums1[nums1.length/2])/2.0;
        	}else{
        		return nums1[nums1.length/2];
        	}
        }
        if(nums1.length == 0){
        	if(nums2.length % 2 == 0){
        		return (nums2[nums2.length/2-1] + nums2[nums2.length/2])/2.0;
        	}else{
        		return nums2[nums2.length/2];
        	}
        }
        if(nums2[0] < nums1[0]){
        	
        }
    	return 0.0;
    }
    
    public static int lengthOfLongestSubstring(String s) {
        char[] arr = s.toCharArray();
        String target = "";
        for(int i = 0 ; i < arr.length ; i++){
        	if(arr[i] == '1'){
        		
        	}
        }
    	return 0;
    }
    
    public static List<List<Integer>> threeSum(int[] nums) {
    	boolean flag = true;
    	List<List<Integer>> list = new ArrayList<List<Integer>>();
    	int l = 0, r = nums.length -1, m = nums.length-2;
    	while(m > l){
    		while(r > m){
    			if(nums[l]+nums[r]+nums[m] == 0 && flag){
					List<Integer> lt = new ArrayList<Integer>();
        			lt.add(nums[l]);
        			lt.add(nums[r]);
        			lt.add(nums[m]);
        			list.add(lt);
        		}
    			r--;
    		}
    		m--;
    	}
    	return list;
    }
    
    public int maxArea(int[] height) {
    	int max = 0;
    	int l = 0, r = height.length - 1;
    	while(l < r){
    		if(height[l] < height[r]){
    			if(height[l]*(r-l) > max){
    				max = height[l]*(r-l);
    			}
    			l++;
    		}else{
    			if(height[r]*(r-l) > max){
    				max = height[r]*(r-l);
    			}
    			r--;
    		}
    	}
    	return max;
    }
    
}
