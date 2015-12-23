// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package karob.bigtrees;

import java.util.Random;
import java.lang.Math;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.init.Blocks;

//import net.minecraft.src.KTreeCfg;

// Referenced classes of package net.minecraft.src:
//            WorldGenerator, World, Block, BlockLeaves, 
//            BlockGrass

public class KWorldGenBigTree extends WorldGenerator
{


    static final byte otherCoordPairs[] = {
        2, 0, 0, 1, 2, 1
    };
    Random rand;
    int rootRand;
    int rootAlt;
    int tapRootRand;
    World worldObj;
    int basePos[] = {
        0, 0, 0
    };
    int heightLimit;
    int height;
    double heightAttenuation;
    double field_875_h;
    double field_874_i;
    double field_873_j;
    double field_872_k;
    int trunkSize;
    int heightLimitLimit;
    int leafDistanceLimit;
    int[][] leafNodes;
    int type;
    Block trunkBlock;
    private int trunkMeta;
    Block leafBlock;
    private int leafMeta;
    private int stuntmin;
    private int heightmin;
    private int heightmax;


    public KWorldGenBigTree(boolean flag)
    {
        super(flag);
        rand = new Random();
        rootRand = 0;
        rootAlt = 0;
        tapRootRand = 0;
        heightLimit = 0; // Tree height
        heightAttenuation = 0.31799999999999999D; // Trunk percentage height
        field_875_h = 1.0D;
        field_874_i = 0.68100000000000001D; // Branch height to base hight stuff
        field_873_j = 1.0D; // Branch length
        field_872_k = 1.0D;
        trunkSize = 1; // Trunk height
        heightLimitLimit = 12; // Height variation
        leafDistanceLimit = 4; // Leaf thickness
		type = 0;
//        KTreeCfg.init();
    }

    void setConfigOptions(Block wood, Block leaf, int woodmeta, int leafmeta, Block Base1, Block Base2, int height1, int height2, int stunt){
	trunkBlock = wood;
	leafBlock = leaf;
	trunkMeta = woodmeta;
	leafMeta = leafmeta;
	heightmin = height1;
	heightmax = height2;
	stuntmin = stunt;
    }


    void setBlockAndMetadata(int par1, int par2, int par3, Block par4, int par5){
        try{
            worldObj.setBlock(par1, par2, par3, par4, par5, 3);
        }catch(RuntimeException e){}
    }

    Block getBlock(int par1, int par2, int par3){
        try{
            return worldObj.getBlock(par1, par2, par3);
        }catch(RuntimeException e){
            return null;
        }
    }

    private boolean generateLeafNodeList()
    {
        //calculate trunk height
        height = (int)((double)heightLimit * heightAttenuation);
        //minimal 1 block branch height
        if(height >= heightLimit) height = heightLimit - 1;
        //try stunting or just eliminate trees growing past the top of the world.
        if(basePos[1] + heightLimit > 256 - 4){
          height = height / 2;
          heightLimit = heightLimit / 2;
          if(height >= heightLimit) height = heightLimit - 1;
          if(height < 1) return false;
          if(basePos[1] + heightLimit > 256 - 4)
            return false;
        }
        int i = (int)(1.3819999999999999D + Math.pow((field_872_k * (double)heightLimit) / 13D, 2D));
        if(i < 1)
        {
            i = 1;
        }
        int ai[][] = new int[i * heightLimit][4];
        int j = (basePos[1] + heightLimit) - leafDistanceLimit;
        int k = 1;
        int l = basePos[1] + height;
        int i1 = j - basePos[1];
        //purely vertical "branch"
        ai[0][0] = basePos[0];
        ai[0][1] = j;
        ai[0][2] = basePos[2];
        ai[0][3] = l;
        j--;
        while(i1 >= 0) 
        {
            int j1 = 0;
            float f = func_528_a(i1); //Conditional AND branch length factor.
              if(k >= i * heightLimit) f = -1.0F;
            if(f < 0.0F) //not time to grow branches
            {
                j--;
                i1--;
            } else // grow branches! All branches grow from one spot, radiating from vertical to horizontal.
            {
                double d = 0.5D;
                for(; j1 < i; j1++)
                {
                    // branch length
                    double d1 = field_873_j * ((double)f * ((double)rand.nextFloat() + 0.32800000000000001D));
                    //if(d1 > 8D) d1 = 8D;
                    //if(d1 < -5D) d1 = -5D;
                    // branch angle (around trunk)
                    double d2 = (double)rand.nextFloat() * 2D * 3.1415899999999999D;
                    int k1 = MathHelper.floor_double(d1 * Math.sin(d2) + (double)basePos[0] + d);
                    int l1 = MathHelper.floor_double(d1 * Math.cos(d2) + (double)basePos[2] + d);
                    int ai1[] = {
                        k1, j, l1
                    };
                    int ai2[] = {
                        k1, j + leafDistanceLimit, l1
                    };
                    if(checkBlockLine(ai1, ai2) != -1)
                    {
                        continue;
                    }
                    int ai3[] = {
                        basePos[0], basePos[1], basePos[2]
                    };
                    //double d3 = Math.sqrt(Math.pow(Math.abs(basePos[0] - ai1[0]), 2D) + Math.pow(Math.abs(basePos[2] - ai1[2]), 2D));
                    //double d4 = d3 * field_874_i;
                    //if((double)ai1[1] - d4 > (double)l)
                    //{
                        ai3[1] = l;
                    //} else
                    //{
                    //    ai3[1] = (int)((double)ai1[1] - d4);
                    //}
                    if(checkBlockLine(ai3, ai1) == -1)
                    {
                        ai[k][0] = k1;
                        ai[k][1] = j;
                        ai[k][2] = l1;
                        ai[k][3] = ai3[1];
                        k++;
                    }
                }

                j--;
                i1--;
            }
        }
// k = number of branches
        leafNodes = new int[k][4];
        System.arraycopy(ai, 0, leafNodes, 0, k);
// leafNodes = { x.branchend, y.branchend, z.branchend, y.branchstart }
		return true;
    }

// GENERATE LEAF BLOCKS
    void func_523_a(int i, int j, int k, float f, byte byte0)
    {
        int i1 = (int)((double)f + 0.61799999999999999D);
        byte byte1 = otherCoordPairs[byte0];
        byte byte2 = otherCoordPairs[byte0 + 3];
        int ai[] = {
            i, j, k
        };
        int ai1[] = {
            0, 0, 0
        };
        int j1 = -i1;
        int k1 = -i1;
        ai1[byte0] = ai[byte0];
        for(; j1 <= i1; j1++)
        {
            ai1[byte1] = ai[byte1] + j1;
            for(int l1 = -i1; l1 <= i1;)
            {
                double d = Math.sqrt(Math.pow((double)Math.abs(j1) + 0.5D, 2D) + Math.pow((double)Math.abs(l1) + 0.5D, 2D));
                if(d > (double)f)
                {
                    l1++;
                } else
                {
                    ai1[byte2] = ai[byte2] + l1;
                    Block i2 = this.getBlock(ai1[0], ai1[1], ai1[2]);
                    if(i2 != Blocks.air && i2 != Blocks.leaves)
                    {
                        l1++;
                    } else
                    {
                        this.setBlockAndMetadata(ai1[0], ai1[1], ai1[2], this.leafBlock, this.leafMeta);
                        //worldObj.setBlock(ai1[0], ai1[1], ai1[2], l);
                        l1++;
                    }
                }
            }

        }

    }

// CHECK IF TIME TO GROW BRANCHES - and partially decide branch length
    float func_528_a(int i)
    {
        if(trunkSize == 0){
            //100% branch density
            return heightLimit - rand.nextFloat();
        }else if(trunkSize == 3){
            //100% branch density
            //if(field_881_b.nextFloat() > 1.0F) return -1.618F;
        }else if(trunkSize > 1){
            //70% branch density
            if(rand.nextFloat() > 0.70F) return -1.618F;
        }
        //Branch tips have to be at least 30% up the tree.
        if(trunkSize == 3){
          if((double)i < (double)(float)heightLimit * 0.19999999999999999D){
            return -1.618F;
          }
	}
        if(trunkSize < 3){
          if((double)i < (double)(float)heightLimit * 0.29999999999999999D){
            return -1.618F;
          }
	}
        if(trunkSize == 4){
          if((double)i < (double)(float)heightLimit * 0.15999999999999999D){
            return -1.618F;
          }
	}
        float f = (float)heightLimit / 2.0F;
        float f1 = (float)heightLimit / 2.0F - (float)i;
        float f2;
        if(f1 == 0.0F)
        {
        //If at middle of tree, pass.
            f2 = f;
        } else
        if(Math.abs(f1) >= f)
        {
        //If off the tree, fail.
            f2 = 0.0F;
        } else
        {
        //This will always pass.
            f2 = (float)Math.sqrt(Math.pow(Math.abs(f), 2D) - Math.pow(Math.abs(f1), 2D));
        }
        f2 *= 0.5F;
        return f2;
    }

//  LEAF GEN CHECK RADIUS
    float func_526_b(int i)
    {
        if(i < 0 || i >= leafDistanceLimit)
        {
            return -1F;
        }
        return i != 0 && i != leafDistanceLimit - 1 ? 3F : 2.0F;
    }

//  GENERATE LEAF BLOCKS somehow
    void generateLeafNode(int i, int j, int k)
    {
        int l = j;
        for(int i1 = j + leafDistanceLimit; l < i1; l++)
        {
            float f = func_526_b(l - j);
            func_523_a(i, l, k, f, (byte)1);
            //func_523_a(i, l, k, f, (byte)1, 18);
        }

    }

// GENERATES WOOD BLOCKS FROM ai TO ai1 (used by trunk and branch)
    void placeBlockLine(int ai[], int ai1[])
    {
        int ai2[] = {
            0, 0, 0
        };
        byte byte0 = 0;
        int j = 0;
        for(; byte0 < 3; byte0++)
        {
            ai2[byte0] = ai1[byte0] - ai[byte0];
            if(Math.abs(ai2[byte0]) > Math.abs(ai2[j]))
            {
                j = byte0;
            }
        }

        if(ai2[j] == 0)
        {
            return;
        }
        byte byte1 = otherCoordPairs[j];
        byte byte2 = otherCoordPairs[j + 3];
        byte byte3;
        if(ai2[j] > 0)
        {
            byte3 = 1;
        } else
        {
            byte3 = -1;
        }
        double d = (double)ai2[byte1] / (double)ai2[j];
        double d1 = (double)ai2[byte2] / (double)ai2[j];
        int ai3[] = {
            0, 0, 0
        };
        int k = 0;
        for(int l = ai2[j] + byte3; k != l; k += byte3)
        {
            ai3[j] = MathHelper.floor_double((double)(ai[j] + k) + 0.5D);
            ai3[byte1] = MathHelper.floor_double((double)ai[byte1] + (double)k * d + 0.5D);
            ai3[byte2] = MathHelper.floor_double((double)ai[byte2] + (double)k * d1 + 0.5D);
            this.setBlockAndMetadata(ai3[0], ai3[1], ai3[2], this.trunkBlock, this.trunkMeta);
            //worldObj.setBlock(ai3[0], ai3[1], ai3[2], i);
        }

    }

// GROW LEAVES FROM BRANCHES
    void generateLeaves()
    {
        int j = this.leafNodes.length;
        for(int i = 0; i < j; i++)
        {
            int k = this.leafNodes[i][0];
            int l = this.leafNodes[i][1];
            int i1 = this.leafNodes[i][2];
            generateLeafNode(k, l, i1);
        }

    }

// Clips off low branches.
    boolean leafNodeNeedsBase(int i)
    {
        if(trunkSize != 2) return true;
        return (double)i >= (double)heightLimit * 0.20000000000000001D;
    }

// GENERATES TRUNK
    void generateTrunk()
    {
        //int qq = 17;
        //int qr = 0;
        //if(trunkSize == 3){
        //    qr = 1;
        //}
        int i = basePos[0];
        int j = basePos[1];
//        if(trunkSize >= 1) j = j - 1;
//        if(trunkSize > 3) j = j - 1;
//if(trunksize == 2) j = j + 1;
        int k = basePos[1] + height + 2;
        int l = basePos[2];
        int ai[] = {
            i, j, l
        };
        int ai1[] = {
            i, k, l
        };
        //Create various trunk sizes.
/*        placeBlockLine(ai, ai1, 17);
        if(trunkSize == 2)
        {
            ai[0]++;
            ai1[0]++;
            placeBlockLine(ai, ai1, 17);
            ai[2]++;
            ai1[2]++;
            placeBlockLine(ai, ai1, 17);
            ai[0]--;
            ai1[0]--;
            placeBlockLine(ai, ai1, 17);
        }
*/
        if(trunkSize == 1)
        {
            placeBlockLine(ai, ai1);
        }
        if(trunkSize == 2)
        {
rootAlt = 0;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            growRoot(ai[0],ai[1]-2,ai[2],5.0/8.0,-1.0/16.0);
rootAlt = 1;
            growRoot(ai[0],ai[1],ai[2],5.7/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[0]++;
            ai1[0]++;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            growRoot(ai[0],ai[1],ai[2],6.3/8.0,-1.0/16.0);
            growRoot(ai[0],ai[1]-2,ai[2],7.0/8.0,-1.0/16.0);
rootAlt = 1;
            growRoot(ai[0],ai[1],ai[2],7.7/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[2]++;
            ai1[2]++;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            growRoot(ai[0],ai[1],ai[2],0.3/8.0,-1.0/16.0);
            growRoot(ai[0],ai[1]-2,ai[2],1.0/8.0,-1.0/16.0);
rootAlt = 1;
            growRoot(ai[0],ai[1],ai[2],1.7/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[0]--;
            ai1[0]--;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            growRoot(ai[0],ai[1],ai[2],2.3/8.0,-1.0/16.0);
            growRoot(ai[0],ai[1]-2,ai[2],3.0/8.0,-1.0/16.0);
rootAlt = 1;
            growRoot(ai[0],ai[1],ai[2],3.7/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            growRoot(ai[0],ai[1],ai[2]-1,4.3/8.0,-1.0/16.0);
        }
        if(trunkSize == 3)
        {
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            placeBlockLine(ai, ai1);
/*
            ai[0]++;
            ai1[0]++;
            placeBlockLine(ai, ai1);
            ai[2]++;
            ai1[2]++;
            placeBlockLine(ai, ai1);
            ai[0]--;
            ai1[0]--;
            placeBlockLine(ai, ai1);
*/
            ai[0]++;
            ai1[0]++;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            placeBlockLine(ai, ai1);
            ai[2]++;
            ai1[2]++;
            ai[0]--;
            ai1[0]--;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            placeBlockLine(ai, ai1);
            ai[2]--;
            ai1[2]--;
            ai[0]--;
            ai1[0]--;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            placeBlockLine(ai, ai1);
            ai[2]--;
            ai1[2]--;
            ai[0]++;
            ai1[0]++;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            placeBlockLine(ai, ai1);
        }
        if(trunkSize == 4)
        {
rootAlt = 10;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            growRoot(ai[0],ai[1]+1,ai[2],5.0/8.0,-1.0/16.0);
/*            growRoot(ai[0],ai[1],ai[2],4.3/8.0,-1.0/16.0);
            growRoot(ai[0],ai[1],ai[2],5.7/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
*/            ai[0]++;
            ai1[0]++;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            growRoot(ai[0],ai[1]+1,ai[2],7.0/8.0,-1.0/16.0);
/*            growRoot(ai[0],ai[1],ai[2],6.3/8.0,-1.0/16.0);
            growRoot(ai[0],ai[1],ai[2],7.7/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
*/            ai[2]++;
            ai1[2]++;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            growRoot(ai[0],ai[1]+1,ai[2],1.0/8.0,-1.0/16.0);
/*            growRoot(ai[0],ai[1],ai[2],0.3/8.0,-1.0/16.0);
            growRoot(ai[0],ai[1],ai[2],1.7/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
*/            ai[0]--;
            ai1[0]--;
            growTapRoot(ai[0],ai[1],ai[2],1.0);
            growRoot(ai[0],ai[1]+1,ai[2],3.0/8.0,-1.0/16.0);
/*            growRoot(ai[0],ai[1],ai[2],2.3/8.0,-1.0/16.0);
            growRoot(ai[0],ai[1],ai[2],3.7/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
*/
            ai[0]--;
            ai1[0]--;
            ai[2]--;
            ai1[2]--;
            growTapRoot(ai[0],ai[1],ai[2],0.5);
            growRoot(ai[0],ai[1]+1,ai[2],4.4/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[0]++;
            ai1[0]++;
            ai[2]--;
            ai1[2]--;
            growTapRoot(ai[0],ai[1],ai[2],0.5);
            growRoot(ai[0],ai[1]+1,ai[2],5.6/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[0]++;
            ai1[0]++;
            growTapRoot(ai[0],ai[1],ai[2],0.5);
            growRoot(ai[0],ai[1]+1,ai[2],6.4/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[0]++;
            ai1[0]++;
            ai[2]++;
            ai1[2]++;
            growTapRoot(ai[0],ai[1],ai[2],0.5);
            growRoot(ai[0],ai[1]+1,ai[2],7.6/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[2]++;
            ai1[2]++;
            growTapRoot(ai[0],ai[1],ai[2],0.5);
            growRoot(ai[0],ai[1]+1,ai[2],0.4/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[0]--;
            ai1[0]--;
            ai[2]++;
            ai1[2]++;
            growTapRoot(ai[0],ai[1],ai[2],0.5);
            growRoot(ai[0],ai[1]+1,ai[2],1.6/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[0]--;
            ai1[0]--;
            growTapRoot(ai[0],ai[1],ai[2],0.5);
            growRoot(ai[0],ai[1]+1,ai[2],2.4/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
            ai[0]--;
            ai1[0]--;
            ai[2]--;
            ai1[2]--;
            growTapRoot(ai[0],ai[1],ai[2],0.5);
            growRoot(ai[0],ai[1]+1,ai[2],3.6/8.0,-1.0/16.0);
            placeBlockLine(ai, ai1);
        }
    }

    private int getMedium(int i, int j, int k){
        //Roots can grow through the following block types.
        Block canGrowOpen[] = {Blocks.air, Blocks.sapling, Blocks.flowing_water, Blocks.water, Blocks.flowing_lava, Blocks.lava, Blocks.log, Blocks.log2, Blocks.leaves, Blocks.leaves2};//more to be re-added
        Block canGrowSolid[] = {Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.gravel}; //more to be re-added
        Block qq = this.getBlock(i, j, k);
        int medium = 0;
        for(int m = 0; m < canGrowOpen.length; m++){
          if(qq==canGrowOpen[m]){
            medium = 1;
            break;
          }
        }
        if(medium==0){
          for(int m = 0; m < canGrowSolid.length; m++){
          if(qq==canGrowSolid[m]){
              medium = 2;
              break;
            }
          }
        }
        return medium;
    }

// Grows roots
    void growTapRoot(int i, int j, int k, double flen)
    {
        if(KTreeCfg.rootsEnable == false) return;
        //int len = 2 + (3*trunkSize) + rand.nextInt(2);
        int med;
        int len = (int)((6.0 + rand.nextFloat()*6.0)*flen);
        if(len == tapRootRand || len == tapRootRand + 1 || len == tapRootRand - 1){
          len = (int)((6.0 + rand.nextFloat()*6.0)*flen);
        }
        for(int jj = 1; jj <= len; jj ++){
          med = getMedium(i, j-jj, k); 
          if(med == 1){
            len -= 1;
          }else if(med == 0){
            len = Math.min(len, jj-1);
            break;
          }
        }
        tapRootRand = len;
        for(int jj = 1; jj <= len; jj ++){
          //zz = world.getBlockId(ai[0], ai[1] - jj, ai[2]);
          //if(zz != 0 && zz != trunkBlock && zz != trunkMeta && zz != 2 && zz != 3 && zz != 8 && zz != 9 && zz != 12 && zz != 13) break;
          //else
          this.setBlockAndMetadata(i, j-jj, k, this.trunkBlock, this.trunkMeta);
        }
    }

// Grows roots
    void growRoot(int l, int m, int n, double theta, double phi)
    {
        if(KTreeCfg.rootsEnable == false) return;
/*        int rr = rand.nextInt(3);
        if(rootRand == 0){
          m -= rr;
          rootRand = rr;
        }else{
          rootRand = 0;
        }
*/
/*        switch(rootRand){
          case 1:
            rootRand = 2;
            break;
          case 2:
            m -= 1;
            rootRand = 3;
            break;
          case 3:
            //m -= 2;
            rootRand = 4;
            break;
          case 4:
            m -= 1;
            rootRand = 0;
            break;
          default:
            rootRand = 0;
        }
*/
/*
        rootRand ++;
        if(rootRand > 5){
          rootAlt = rand.nextInt(2);
          rootRand = 0;
        }else{
          if(rootAlt == 1){
            m --;
            rootAlt = 0;
          }else{
            rootAlt = 1;
          }
        }*/
        if(rootAlt == 1){
          rootRand = rand.nextInt(2);
          m -= rootRand;
          rootAlt = 2;
        }else if(rootAlt == 2){
          if(rootRand == 0)
            m -= 1;
          rootAlt = 0;
        }else if(rootAlt == 10){
          m -= rand.nextInt(2);
        }
        m += 1;
        phi -= (double)rand.nextFloat()*0.05;
        theta += (double)rand.nextFloat()*0.1 - 0.05;
        double direction = (2.0*Math.PI) * theta;
        double curl = rand.nextFloat()*0.4F - 0.2F;
        double pitch = (2.0*Math.PI) * phi;
        int length = 2 + (3*trunkSize) + rand.nextInt(2);
        double x, y, z;
        if(l > 0) x = (double)l + 0.5;
        else x = (double)l - 0.5;
        //double y = (double)basePos[1] + 0.5;
        y = (double)m + 0.5;
        if(n > 0) z = (double)n + 0.5;
        else z = (double)n - 0.5;
        double x2, y2, z2, hoz;
        int i = (int)x; int j = (int)y; int k = (int)z;
        int i2, j2, k2, di, dk;
        int med = getMedium(i, j, k); //Check the "Medium" of a block for root growing - solid, open, or forbidden.
        int cnt = 0;
        while(length > 0.0){
          length --;
//          direction = direction + curl;
          curl = curl + rand.nextFloat()*0.06F - 0.03F;
          if(med == 1){ //Root growing in openness.
            pitch = (pitch + Math.PI/2.0)*0.7 - Math.PI/2.0;
//            if(pitch > 0.0){
//              pitch = pitch - 10.0*Math.PI/180.0;
//            }else{
//              pitch = (pitch + Math.PI/2.0)*0.7 - Math.PI/2.0;
//            }
          }else{ //Root growing in solid.
            pitch = (pitch + Math.PI/2.0)*0.9 - Math.PI/2.0;
          }

          hoz = Math.cos(pitch);
          x2 = x + Math.cos(direction)*hoz;
          y2 = y + Math.sin(pitch);
          z2 = z + Math.sin(direction)*hoz;
          i2 = (int)x2; j2 = (int)y2; k2 = (int)z2;
        if(i2 != i || j2 != j || k2 != k){
          this.setBlockAndMetadata(i, j, k, this.trunkBlock, this.trunkMeta); //1);
          cnt ++;
          if(cnt < 4){
            if(j2 != j-1 || i2 != i || k2 != k)
            this.setBlockAndMetadata(i, j-1, k, this.trunkBlock, this.trunkMeta);
          }
          med = getMedium(i2, j2, k2);
          if(med != 0){ //Grow normal.
            x = x2; y = y2; z = z2; i = i2; j = j2; k = k2;
          }else{ //Try to grow down now.
            med = getMedium(i, j-1, k);
            if(med != 0){ //Grow down.
              y = y - 1.0; j = j - 1; pitch = -Math.PI/2.0;
            }else{ //Try to grow out now.
              x2 = x + Math.cos(direction);
              z2 = z + Math.sin(direction);
              i2 = (int)x2; k2 = (int)z2;
              med = getMedium(i2, j, k2);
              if(med != 0){ //Grow out.
                x = x2; z = z2; i = i2; k = k2; pitch = 0.0;
              }else{ //Try bending now.
                int dir = ((int)(direction*8.0/Math.PI)); //Integer direction - 16 = complete rotation.
                if(dir < 0) dir = 15 - (15-dir) % 16;
                else dir = dir % 16;
                int pol = dir % 2; //'Polarity' of bending root - preferred bending direction.
                di = i2 - i; dk = k2 - k;
                int[] tdir = {0, 0, 0, 0}; //Testing directions.
                if(di == 0 && dk == 0){
                  if(dir < 1){di=1;dk=0;}
                  else if(dir < 3){di=1;dk=1;}
                  else if(dir < 5){di=0;dk=1;}
                  else if(dir < 7){di=-1;dk=1;}
                  else if(dir < 9){di=-1;dk=0;}
                  else if(dir < 11){di=-1;dk=-1;}
                  else if(dir < 13){di=0;dk=-1;}
                  else if(dir < 15){di=1;dk=-1;}
                  else{di=1;dk=0;}
                }
                if(dk == 0){
                  if(di > 0){
                    if(pol==1){
                      tdir[0] = 2;
                      tdir[1] = 14;
                      tdir[2] = 4;
                      tdir[3] = 12;
                    }else{
                      tdir[0] = 14;
                      tdir[1] = 2;
                      tdir[2] = 12;
                      tdir[3] = 4;
                    }
                  }else{
                    if(pol==1){
                      tdir[0] = 6;
                      tdir[1] = 10;
                      tdir[2] = 4;
                      tdir[3] = 12;
                    }else{
                      tdir[0] = 10;
                      tdir[1] = 6;
                      tdir[2] = 12;
                      tdir[3] = 4;
                    }
                  }
                }else if(di == 0){
                  if(dk > 0){
                    if(pol==1){
                      tdir[0] = 2;
                      tdir[1] = 6;
                      tdir[2] = 0;
                      tdir[3] = 8;
                    }else{
                      tdir[0] = 6;
                      tdir[1] = 2;
                      tdir[2] = 8;
                      tdir[3] = 0;
                    }
                  }else{
                    if(pol==1){
                      tdir[0] = 10;
                      tdir[1] = 14;
                      tdir[2] = 8;
                      tdir[3] = 0;
                    }else{
                      tdir[0] = 14;
                      tdir[1] = 10;
                      tdir[2] = 0;
                      tdir[3] = 8;
                    }
                  }
                }else if(dk > 0){
                  if(di > 0){
                    if(pol==1){
                      tdir[0] = 0;
                      tdir[1] = 4;
                      tdir[2] = 14;
                      tdir[3] = 6;
                    }else{
                      tdir[0] = 4;
                      tdir[1] = 0;
                      tdir[2] = 6;
                      tdir[3] = 14;
                    }
                  }else{
                    if(pol==1){
                      tdir[0] = 4;
                      tdir[1] = 8;
                      tdir[2] = 2;
                      tdir[3] = 10;
                    }else{
                      tdir[0] = 8;
                      tdir[1] = 4;
                      tdir[2] = 10;
                      tdir[3] = 2;
                    }
                  }
                }else{
                  if(di > 0){
                    if(pol==1){
                      tdir[0] = 12;
                      tdir[1] = 0;
                      tdir[2] = 10;
                      tdir[3] = 2;
                    }else{
                      tdir[0] = 0;
                      tdir[1] = 12;
                      tdir[2] = 2;
                      tdir[3] = 10;
                    }
                  }else{
                    if(pol==1){
                      tdir[0] = 8;
                      tdir[1] = 12;
                      tdir[2] = 6;
                      tdir[3] = 14;
                    }else{
                      tdir[0] = 12;
                      tdir[1] = 8;
                      tdir[2] = 14;
                      tdir[3] = 6;
                    }
                  }
                }
                for(int q = 0; q < 4; q++){
                  if(tdir[q] == 0){
                    di = 1; dk = 0;
                  }else if(tdir[q] == 2){
                    di = 1; dk = 1;
                  }else if(tdir[q] == 4){
                    di = 0; dk = 1;
                  }else if(tdir[q] == 6){
                    di = -1; dk = 1;
                  }else if(tdir[q] == 8){
                    di = -1; dk = 0;
                  }else if(tdir[q] == 10){
                    di = -1; dk = -1;
                  }else if(tdir[q] == 12){
                    di = 0; dk = -1;
                  }else{
                    di = 1; dk = -1;
                  }
                  i2 = i + di; k2 = k + dk;
                  med = getMedium(i2, j, k2);
                  if(med != 0){
                    i = i2; k = k2; x = (double)i + 0.5; z = (double)k + 0.5;
                    pitch = 0;
                    direction = (double)tdir[q] * 2.0*Math.PI/16.0;
                    break;
                  }
                }
                if(med == 0) return; //Root cannot grow any further.
              }
            }
          }
        }
        }
//                while(direction < 0.0){direction = direction + 2.0*Math.PI;}
//                while(direction > 2.0*Math.PI){direction = direction - 2.0*Math.PI;}
    }

// GENERATES BRANCHES
    void generateLeafNodeBases()
    {
        //int qq = 17;
        //int qr = 0;
        //if(trunkSize == 3){
        //    qr = 1;
        //}
        int i = 0;
        int j = leafNodes.length;
        int ai[] = {
            basePos[0], basePos[1], basePos[2]
        };
        for(; i < j; i++)
        {
            int ai1[] = leafNodes[i];
            int ai2[] = {
                ai1[0], ai1[1], ai1[2]
            };
            ai[1] = ai1[3];
            int k = ai[1] - basePos[1];
            if(leafNodeNeedsBase(k))
            {
                //placeBlockLine(ai, ai2, 17);
                placeBlockLine(ai, ai2);
            }
        }

    }

// CHECKS IF STUFF IS IN AIR/LEAVES OR IN SOLID
    int checkBlockLine(int ai[], int ai1[])
    {
        int ai2[] = {
            0, 0, 0
        };
        byte byte0 = 0;
        int i = 0;
        for(; byte0 < 3; byte0++)
        {
            ai2[byte0] = ai1[byte0] - ai[byte0];
            if(Math.abs(ai2[byte0]) > Math.abs(ai2[i]))
            {
                i = byte0;
            }
        }

        if(ai2[i] == 0)
        {
            return -1;
        }
        byte byte1 = otherCoordPairs[i];
        byte byte2 = otherCoordPairs[i + 3];
        byte byte3;
        if(ai2[i] > 0)
        {
            byte3 = 1;
        } else
        {
            byte3 = -1;
        }
        double d = (double)ai2[byte1] / (double)ai2[i];
        double d1 = (double)ai2[byte2] / (double)ai2[i];
        int ai3[] = {
            0, 0, 0
        };
        int j = 0;
        int k = ai2[i] + byte3;
        do
        {
            if(j == k)
            {
                break;
            }
            ai3[i] = ai[i] + j;
            ai3[byte1] = MathHelper.floor_double((double)ai[byte1] + (double)j * d);
            ai3[byte2] = MathHelper.floor_double((double)ai[byte2] + (double)j * d1);
            Block l = this.getBlock(ai3[0], ai3[1], ai3[2]);
            if(l != Blocks.air && l != Blocks.leaves && l != Blocks.log)
            {
                break;
            }
            j += byte3;
        } while(true);
        if(j == k)
        {
            return -1;
        } else
        {
            return Math.abs(j);
        }
    }

    boolean validTreeLocation()
    {
/*        int ai[] = {
            basePos[0], basePos[1], basePos[2]
        };
        int ai1[] = {
            basePos[0], (basePos[1] + heightLimit) - 1, basePos[2]
        };
*/
        Block i = this.getBlock(basePos[0], basePos[1] - 1, basePos[2]);
      //if(basePos[1] + heightLimit >= 80) return false;
        if(i != Blocks.grass && i != Blocks.dirt && i != Blocks.sand) //Can grow tree on dirt, grass, or sand...
        {
            return false;
        }
/*
        int j = checkBlockLine(ai, ai1);
        if(j == -1)
        {
            return true;
        }
        if(j < 6)
        {
            return false;
        } else
        {
            heightLimit = j;
            return true;
//            return false;
        }
*/
        return true;
    }

    public void func_517_a(double d, double d1, double d2)
    {
        heightLimitLimit = (int)(d * 12D);
/*        if(d > 0.5D)
        {
            leafDistanceLimit = 5;
        }
        field_873_j = d1;
        field_872_k = d2;
*/    }

    public boolean generate(World world, Random random, int i, int j, int k)
    {
		type = 0;
		return generator(world, random, i, j, k);
	}

    public boolean desertGenerate(World world, Random random, int i, int j, int k)
    {
		type = 1;
		return generator(world, random, i, j, k);
	}

    public boolean desertGenerate2(World world, Random random, int i, int j, int k)
    {
		type = 11;
		return generator(world, random, i, j, k);
	}

    public boolean swampGenerate(World world, Random random, int i, int j, int k)
    {
		type = 2;
		return generator(world, random, i, j, k);
	}

    public boolean greatGenerate(World world, Random random, int i, int j, int k)
    {
		type = 3;
		return generator(world, random, i, j, k);
	}

    public boolean pineGenerate(World world, Random random, int i, int j, int k)
    {
		type = 4;
		return generator(world, random, i, j, k);
	}

    public boolean blockOakGenerate(World world, Random random, int i, int j, int k)
    {
        type = 5;
        return generator(world, random, i, j, k);
    }

    public boolean birchGenerate(World world, Random random, int i, int j, int k)
    {
        type = 6;
        return generator(world, random, i, j, k);
    }

    private boolean generator(World world, Random random, int i, int j, int k)
    {
        worldObj = world;
        long l = random.nextLong();
        rand.setSeed(l);
        basePos[0] = i;
        basePos[1] = j;
        basePos[2] = k;
        int qq;
        boolean qbirch = false;
/*        if(heightLimit == 0)
        {
            heightLimit = 5 + rand.nextInt(heightLimitLimit);
        }
*/
        if(type == 1) qq = rand.nextInt(8) + 86; // Dead desert trees.
        else if(type == 2) qq = 0; // Big swamp trees.
        else if(type == 3) qq = 0; // Great oak trees.
        else if(type == 4) qq = 30; // Large pine trees.
        else if(type == 5 || type == 6) qq = 80; // Block oak trees/ birch.
        else qq = rand.nextInt(94); //kam - sponge plant is turned off
        if(type == 6) qbirch = true;
        if(type == 11){
            type = 1;
            qq = 87;
        }
//qq=89;
	int[] heightvector = {heightmin, heightmax-heightmin};
	heightLimit = KTreeCfg.vary(rand,heightvector);
        if(qq < 8){
            //WIDE TREE
            //heightLimit = KTreeCfg.vary(rand,oak2Height); //Tree Height
            heightAttenuation = 0.1D; //Trunk Percentage Height
            field_873_j = 1.4D; //Branch Length
            trunkSize = 4; //Trunk Width
            heightLimitLimit = 4; //Height Variation
            leafDistanceLimit = 4; //Leaf Thickness
            //trunkBlock = woodBlock;
            //trunkMeta = woodMeta;
            //leafBlock = leafBlock;
            //leafMeta = leafMeta;
        }else if(qq < 40){
            //TALL TREE
            //heightLimit = KTreeCfg.vary(rand,KTreeCfg.pine1Height); //Tree Height
            heightAttenuation = 0.3D; //Trunk Percentage Height
            field_873_j = 1.2D; //Branch Length
            trunkSize = 3; //Trunk Width
            heightLimitLimit = 3; //Height Variation
            leafDistanceLimit = 4; //Leaf Thickness
            //trunkBlock = Block.getBlockById(KTreeCfg.pine1WoodType);
            //trunkMeta = KTreeCfg.pine1WoodMeta;
            //leafBlock = Block.getBlockById(KTreeCfg.pine1LeafType);
            //leafMeta = KTreeCfg.pine1LeafMeta;
        }else if(qq < 90){
            //BIGGER TREE
            //heightLimit = KTreeCfg.vary(rand,KTreeCfg.oak1Height); //Tree Height
            heightAttenuation = 0.3D; //Trunk Percentage Height
            field_873_j = 1.0D; //Branch Length
            trunkSize = 2; //Trunk Width
            heightLimitLimit = 3; //Height Variation
            leafDistanceLimit = 4; //Leaf Thickness
            //trunkBlock = Block.getBlockById(KTreeCfg.oak1WoodType);
            //trunkMeta = KTreeCfg.oak1WoodMeta;
            //leafBlock = Block.getBlockById(KTreeCfg.oak1LeafType);
            //leafMeta = KTreeCfg.oak1LeafMeta;
            if(qbirch){
                heightLimit = KTreeCfg.vary(rand,KTreeCfg.birchHeight);
                //trunkBlock = Block.getBlockById(KTreeCfg.birchWoodType);
                //trunkMeta = KTreeCfg.birchWoodMeta;
                //leafBlock = Block.getBlockById(KTreeCfg.birchLeafType);
                //leafMeta = KTreeCfg.birchLeafMeta;
            }
        }else if(qq < 95){
            //DEAD TREE
            //heightLimit = KTreeCfg.vary(rand,KTreeCfg.stubHeight); //Tree Height
            heightAttenuation = 0.3D; //Trunk Percentage Height
            field_873_j = 1.0D; //Branch Length
            trunkSize = 2; //Trunk Width
            heightLimitLimit = 3; //Height Variation
            leafDistanceLimit = 0; //Leaf Thickness
            //trunkBlock = Block.getBlockById(KTreeCfg.stubWoodType);
            //trunkMeta = KTreeCfg.stubWoodMeta;
        }else{
            //VINEY FUNGUS
            heightLimit = 5; //Tree Height
            heightAttenuation = 0.1D; //Trunk Percentage Height
            field_873_j = 2.0D; //Branch Length
            trunkSize = 0; //Trunk Width
            heightLimitLimit = 0; //Height Variation
            leafDistanceLimit = 0; //Leaf Thickness
            trunkBlock = Blocks.sponge; //Sponge
            leafBlock = Blocks.sponge;
        }
        if(type == 1){
            if(trunkSize != 1){
              field_873_j = field_873_j * 1.0D; //Double branch length on desert trees.
              //heightLimit = KTreeCfg.vary(rand,KTreeCfg.deadHeight); //Tree Height
            }
            leafDistanceLimit = 0; //No leaves on desert trees.
            //trunkBlock = Block.getBlockById(KTreeCfg.deadWoodType);
            //trunkMeta = KTreeCfg.deadWoodMeta;
        }else if(type == 2){
            //heightLimit = KTreeCfg.vary(rand,KTreeCfg.swoakHeight); //Tree Height
            heightAttenuation = 0.0D; //Lower branches on swamp trees.
            //trunkBlock = Block.getBlockById(KTreeCfg.swoakWoodType);
            //trunkMeta = KTreeCfg.swoakWoodMeta;
            //leafBlock = Block.getBlockById(KTreeCfg.swoakLeafType);
            //leafMeta = KTreeCfg.swoakLeafMeta;
        }
        if(heightLimitLimit > 0)
            heightLimit = heightLimit + rand.nextInt(heightLimitLimit*2) - heightLimitLimit;
        if(!validTreeLocation())
        {
            return false;
        } else
        {
            rootRand = rand.nextInt(4);
            if(generateLeafNodeList()){ //Generate tree and branch arrays.
//world.lightUpdates = false;
             generateLeaves(); //Grow leaves from branches.
             generateTrunk(); //Add trunk blocks to world.
             generateLeafNodeBases(); //Add branch blocks to world.
//world.lightUpdates = true;
             return true;
            }else{return false;}
        }

/*        if(!validTreeLocation())
        {
            return false;
        } else
        {
            generateLeafNodeList();
            generateLeaves();
            generateTrunk();
            generateLeafNodeBases();
            return true;
        }
*/
    }
}
