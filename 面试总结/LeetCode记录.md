#### 1.无重复子字符串的最大长度。

* 滑动窗口

~~~java
    //时间和空间都是消耗最小的解法。
    public int lengthOfLongestSubstring2(String s) {
        if (s.length() == 0) return 0;
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        int max = 0;//最长子串长度
        int left = 0;//滑动窗口左下标，i相当于滑动窗口右下标
        for (int i = 0; i < s.length(); i++) {
            if (map.containsKey(s.charAt(i))) {//charAt() 方法用于返回指定索引处的字符。索引范围为从 0 到 length() - 1。
                left = Math.max(left, map.get(s.charAt(i)) + 1);       //map.get():返回字符所对应的索引，当发现重复元素时，窗口左指针右移

            }
            map.put(s.charAt(i), i);      //更新map中相同字符的下标
            max = Math.max(max, i - left + 1); //比较最大值
        }
        return max;
    }
~~~



#### 2.寻找两个正序数组的中位数。

* 第k小数

  ~~~java
      public double findMedianSortedArrays3(int[] A, int[] B) {
          int len1 = A.length;
          int len2 = B.length;
          if (len1 == 0 && len2 == 0)
              return 0;
          //k非索引，是第几位。
          int len = len1 + len2;
          //分奇数和偶数的情况。
          int r1;
          int r2;
          if (len % 2 == 0) {
             int k1 = len / 2;
             int k2 = k1 + 1;
              r1 = getKthElement(A, B, k1);
              r2 = getKthElement(A, B, k2);
          } else {
              int k1 = len / 2 + 1;
              r2 = r1 = getKthElement(A, B, k1);
          }
          return (r1 + r2) / 2d;
      }
   public int getKthElement(int[] nums1, int[] nums2, int k) {
          int len1 = nums1.length;
          int len2 = nums2.length;
          int p1 = 0;
          int p2 = 0;
          while (true) {
              //1.临界条件。有一个数组越界 或 k=1;
              if (p1 == len1) {
                  return nums2[p2 + k - 1];
              }
  
              if (p2 == len2) {
                  return nums1[p1 + k - 1];
              }
  
              if (k == 1) {
                  return Math.min(nums1[p1], nums2[p2]);
              }
  
              //2.移动比较
              int r = k / 2;//r是位数,r-1是因为p已经移动过一位了。newP1在比较的时候移动到新位置的索引，临时的。
              int newP1 = Math.min(p1 + r - 1, len1 - 1);//防止越界
              int newP2 = Math.min(p2 + r - 1, len2 - 1);//防止越界
  
              if (nums1[newP1] <= nums2[newP2]) {
                  k = k - (newP1 - p1 + 1);//k实际减少的数量
                  p1 = newP1 + 1;//移动到下一位。
              } else {
                  k = k - (newP2 - p2 + 1);
                  p2 = newP2 + 1;
              }
  
          }
  
      }
  ~~~

  

#### 3. 查找给定字符串中的最长回文字符串。

> 回文字符串就是两边对称的字符串

* 中心扩散

  ~~~java
      public String longestPalindrome2(String s) {
          if (s == null || s.length() == 0)
              return "";
          if (s.length() == 1)
              return s;
          String maxStr = String.valueOf(s.charAt(0));
          for (int i = 1; i < s.length(); i++) {
              char c1 = s.charAt(i);
              char c2 = s.charAt(i - 1);
              String s1 = loopback(s, i, true);
              if (s1.length() > maxStr.length())
                  maxStr = s1;
              if (c1 == c2) {//偶数的话多一次回环验证
                  String s2 = loopback(s, i, false);
                  if (s2.length() > maxStr.length())
                      maxStr = s2;
              }
          }
          return maxStr;
      }
  
      public String loopback(String s, int index, boolean isOdd) {
          //分奇数和偶数的情况，初始指针不同。
          int left = 0;
          int right = 0;
          int len = s.length();
          if (isOdd) {//奇数
              left = index - 1;
              right = index + 1;
          } else {//偶数
              left = index - 1;
              right = index;
          }
  
          while (true) {
              //1.边界条件:左右越界，左右不等。
              if (left < 0){
                  left=0;
                  right--;
                  break;
              }
  
              if (right > len - 1){
                  right = len-1;
                  left++;
                  break;
              }
  
              char c1 = s.charAt(left);
              char c2 = s.charAt(right);
              if (c1 != c2) {
                  //退回最大回环的地方
                  left++;
                  right--;
                  break;
              }
              //左右扩散
              left--;
              right++;
          }
          String substring = s.substring(left, right + 1);
          //System.out.println("subString:"+substring);
          return substring;
      }
  ~~~

  

#### 4.整数反转。

~~~java
    //时间和空间都是最优，击败100%用户。
    public int reverse2(int x) {
        long y = 0;
        while (x != 0) {
            y = y * 10 + x % 10;
            x = x / 10;
            if (y >= Integer.MAX_VALUE || y <= Integer.MIN_VALUE)
                return 0;
        }
        return (int) y;
    }
~~~

