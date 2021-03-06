/** @file CWTriangle.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief face triangle
 *
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 */
package com.topodroid.Cave3D;

import java.util.List;
import java.io.PrintWriter;
// import java.io.PrintStream;
// import java.io.IOException;

import android.util.Log;

public class CWTriangle extends CWFacet
{
  private static int cnt = 0;
  static void resetCounter() { cnt = 0; }

  int mCnt;
  CWSide s1, s2, s3;

  final static int TRIANGLE_NORMAL = 0;
  final static int TRIANGLE_HIDDEN = 1;
  final static int TRIANGLE_SPLIT  = 2;
  int mType;
  
  // private Cave3DVector mVolume;
  // private float mVolumeOffset;

  private boolean mOutside; // work variable
  

  CWSide nextWithPoint( CWSide s, CWPoint p )
  {
    if ( s == s1 ) {
  	  if ( s2.contains(p) ) return s2;
  	  if ( s3.contains(p) ) return s3;
    } else if ( s == s2 ) {
  	  if ( s1.contains(p) ) return s1;
  	  if ( s3.contains(p) ) return s3;
    } else if ( s == s3 ) {
  	  if ( s1.contains(p) ) return s1;
  	  if ( s2.contains(p) ) return s2;
    }
    return null;
  }
  
  CWSide leftSideOf( CWPoint p ) // left is prev
  {
    if ( p == v1 ) return s2;
    if ( p == v2 ) return s3;
    if ( p == v3 ) return s1;
    return null;
  }
  
  CWSide rightSideOf( CWPoint p ) // right is next
  {
    if ( p == v1 ) return s3;
    if ( p == v2 ) return s1;
    if ( p == v3 ) return s2;
    return null;
  }
  
  CWSide oppositeSideOf( CWPoint p )
  {
    if ( p == v1 ) return s1;
    if ( p == v2 ) return s2;
    if ( p == v3 ) return s3;
    return null;
  }
  
  CWPoint oppositePointOf( CWSide s )
  {
    if ( s == s1 ) return v1;
    if ( s == s2 ) return v2;
    if ( s == s3 ) return v3;
    return null;
  }

  /** create a triangle on three points
   * Note each side can be null (if so it is created)
   */
  public CWTriangle( CWPoint v1, CWPoint v2, CWPoint v3, CWSide s1, CWSide s2, CWSide s3 )
  {
    super( v1, v2, v3 );
    mCnt  = cnt ++;
    mType = TRIANGLE_NORMAL;
    buildTriangle( s1, s2, s3 );
  }
  
  public CWTriangle( int tag, CWPoint v1, CWPoint v2, CWPoint v3, CWSide s1, CWSide s2, CWSide s3 )
  {
    super( v1, v2, v3 );
    mCnt  = tag;
    if ( cnt <= tag ) cnt = tag+1;
    mType = TRIANGLE_NORMAL; // FIXME
    buildTriangle( s1, s2, s3 );
  }
  
  void rebuildTriangle()
  {
    v1.removeTriangle( this );
    v2.removeTriangle( this );
    v3.removeTriangle( this );
    // buildFacet( v1, v2, v3 ); // FIXME maybe not necessary
    buildTriangle( s1, s2, s3 );
  }
  
  private void buildTriangle( CWSide s1, CWSide s2, CWSide s3 )
  {
    this.s1 = (s1 == null) ? new CWSide( v2, v3 ) : s1;
    this.s2 = (s2 == null) ? new CWSide( v3, v1 ) : s2;
    this.s3 = (s3 == null) ? new CWSide( v1, v2 ) : s3;
    this.s1.setTriangle( this );
    this.s2.setTriangle( this );
    this.s3.setTriangle( this );
    this.v1.addTriangle( this );
    this.v2.addTriangle( this );
    this.v3.addTriangle( this );
    // mVolume = new Cave3DVector(
    //   ( v1.y * v2.z + v3.y * v1.z + v2.y * v3.z - v1.y * v3.z - v3.y * v2.z - v2.y * v1.z ),
    //   ( v1.x * v3.z + v3.x * v2.z + v2.x * v1.z - v1.x * v2.z - v3.x * v1.z - v2.x * v3.z ),
    //   ( v1.x * v2.y + v3.x * v1.y + v2.x * v3.y - v1.x * v3.y - v3.x * v2.y - v2.x * v1.y ) );
    // mVolumeOffset = v1.x * (v2.y*v3.z - v2.z*v3.y) + v1.y * (v2.z*v3.x - v2.x*v3.z) + v1.z * (v2.x*v3.y - v2.y*v3.z);

  }

  void dump( )
  {
    Log.v("Cave3D", "Tri " + mCnt + " " + mType + " V " + v1.mCnt + " " + v2.mCnt + " " + v3.mCnt 
                     + " S " + s1.mCnt + " " + s2.mCnt + " " + s3.mCnt 
                     // + " U " + un.x + " " + un.y + " " + un.z
    );
  }
  
  void serialize( PrintWriter out )
  {
    out.format( "T %d %d %d %d %d %d %d %d %.3f %.3f %.3f\n",
                mCnt, mType, v1.mCnt, v2.mCnt, v3.mCnt, s1.mCnt, s2.mCnt, s3.mCnt, un.x, un.y, un.z );
  }

  /* if vector P is "outside" the triangle-plane (ie on the other side than the hull)
   * set mOutside to true.
   * P is outside if the volume of the tetrahedron of P and the triangle is negative
   * because the normal U of the triangle points "inside" the convex hull
   */
  boolean setOutside( Cave3DVector p )
  {
    mOutside = ( volume(p) < 0.0f );
    return mOutside;
  }
  
  boolean isOutside() { return mOutside; }
  
  /* returns true is S is a side of the triangle
   */
  boolean contains( CWSide s ) { return s == s1 || s == s2 || s == s3; } 
  
  /*                 |
               ,.--''+ beta3: v1 + b3 u3   alpha3, s2
      s2   v3 '      |
      u3  .^^.s1     |
         /    \ u1   |
       v1 ----> v2 --+ beta2: v1 + b2 u2   alpha2, s3
           u2     `-.|
           s3        + beta1: v2 + b1 u1   alpha1, s1
   */
  boolean intersectionPoints( Cave3DVector v, Cave3DVector n, CWLinePoint lp1, CWLinePoint lp2 )
  {
    // round beta to three decimal digits
    float b2 = ((int)(beta2( v, n )*1000.1))/1000.0f;
    float b3 = ((int)(beta3( v, n )*1000.1))/1000.0f;
    float b1 = ((int)(beta1( v, n )*1000.1))/1000.0f;
    float a1, a2, a3;
    if ( b1 >= 0 && b1 <= 1 ) {
      a1 = alpha1( v, n );
      lp1.copy( a1, s1, this, v.plus( n.times(a1) ) );
      if ( b2 >= 0 && b2 <= 1 ) {
        a2 = alpha2( v, n );
        lp2.copy( a2, s3, this, v.plus( n.times(a2) ) );
        // Log.v("Cave3D", "Tri " + mCnt + " b1 " + b1 + " b2 " + b2 );
        return true;
      } else if ( b3 >= 0 && b3 <= 1 ) {
        a3 = alpha3( v, n );
        lp2.copy( a3, s2, this, v.plus( n.times(a3) ) );
        // Log.v("Cave3D", "Tri " + mCnt + " b1 " + b1 + " b3 " + b3 );
        return true;
      }
    } else if ( b2 >= 0 && b2 <= 1 ) {
      a2 = alpha2( v, n );
      lp1.copy( a2, s3, this, v.plus( n.times(a2) ) );
      if ( b3 >= 0 && b3 <= 1 ) {
        a3 = alpha3( v, n );
        lp2.copy( a3, s2, this, v.plus( n.times(a3) ) );
        // Log.v("Cave3D", "Tri " + mCnt + " b2 " + b2 + " b3 " + b3 );
        return true;
      }
    }
    return false;
  }
  
  // ============================================================================================

}
