/*    */ package org.webrtc;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Size
/*    */ {
/*    */   public int width;
/*    */   public int height;
/*    */   
/*    */   public Size(int width, int height) {
/* 22 */     this.width = width;
/* 23 */     this.height = height;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 28 */     return this.width + "x" + this.height;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 33 */     if (!(other instanceof Size)) {
/* 34 */       return false;
/*    */     }
/* 36 */     Size otherSize = (Size)other;
/* 37 */     return (this.width == otherSize.width && this.height == otherSize.height);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 43 */     return 1 + 65537 * this.width + this.height;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/Size.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */