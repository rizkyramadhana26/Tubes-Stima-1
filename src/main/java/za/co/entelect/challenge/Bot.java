package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;
import java.lang.Math;

import static java.lang.Math.max;

import java.security.SecureRandom;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Command> directionList = new ArrayList<>();

    private final Random random;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();
    private final static Command DO_NOTHING = new DoNothingCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot() {
        this.random = new SecureRandom();
        directionList.add(TURN_LEFT);
        directionList.add(TURN_RIGHT);
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        List<Object> viewableBlocks = getBlocksInFront(myCar.position.lane, myCar.position.block, gameState);
        List<Object> blocks = viewableBlocks.subList(0,myCar.speed+1);
        List<Object> blocksIfAccelerating = viewableBlocks.subList(0,nextSpeedState(myCar.speed,myCar.damage)+1);
        List<Object> blocksIfBoost = viewableBlocks.subList(0,speedIfBoost(myCar.damage)+1);
        //jika masih menggunakan boost, prioritaskan strategi menghindari rintangan
        if(myCar.speed!=15){
            //perbaiki bila rusak darurat
            if (myCar.damage > 3) {
                return FIX;
            }
            //jalan terlebih dahulu bila berhenti
            if(myCar.speed==0){
                return ACCELERATE;
            }
            //jika stok powerups sudah menipis, ambil dulu.
            if(countPowerups(myCar.powerups, PowerUps.BOOST)<=2){
                if(blocks.contains(Terrain.BOOST)){
                    return DO_NOTHING;
                }else if(myCar.position.lane==1){
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(right.contains(Terrain.BOOST)){
                        return TURN_RIGHT;
                    }
                }else if(myCar.position.lane==4){
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.BOOST)){
                        return TURN_LEFT;
                    }
                }else{
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.BOOST)){
                        return TURN_LEFT;
                    } else if(right.contains(Terrain.BOOST)){
                        return TURN_RIGHT;
                    }
                }
            }
    
            if(countPowerups(myCar.powerups, PowerUps.OIL)<=2){
                if(blocks.contains(Terrain.OIL_POWER)){
                    return DO_NOTHING;
                }else if(myCar.position.lane==1){
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(right.contains(Terrain.OIL_POWER)){
                        return TURN_RIGHT;
                    }
                }else if(myCar.position.lane==4){
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.OIL_POWER)){
                        return TURN_LEFT;
                    }
                }else{
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.OIL_POWER)){
                        return TURN_LEFT;
                    } else if(right.contains(Terrain.OIL_POWER)){
                        return TURN_RIGHT;
                    }
                }
            }

            if(countPowerups(myCar.powerups, PowerUps.LIZARD)<=2){
                if(blocks.contains(Terrain.LIZARD)){
                    return DO_NOTHING;
                }else if(myCar.position.lane==1){
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(right.contains(Terrain.LIZARD)){
                        return TURN_RIGHT;
                    }
                }else if(myCar.position.lane==4){
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.LIZARD)){
                        return TURN_LEFT;
                    }
                }else{
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.LIZARD)){
                        return TURN_LEFT;
                    } else if(right.contains(Terrain.LIZARD)){
                        return TURN_RIGHT;
                    }
                }
            }

            if(countPowerups(myCar.powerups, PowerUps.TWEET)<=2){
                if(blocks.contains(Terrain.TWEET)){
                    return DO_NOTHING;
                }else if(myCar.position.lane==1){
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(right.contains(Terrain.TWEET)){
                        return TURN_RIGHT;
                    }
                }else if(myCar.position.lane==4){
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.TWEET)){
                        return TURN_LEFT;
                    }
                }else{
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.TWEET)){
                        return TURN_LEFT;
                    } else if(right.contains(Terrain.TWEET)){
                        return TURN_RIGHT;
                    }
                }
            }

            if(countPowerups(myCar.powerups, PowerUps.EMP)<=2){
                if(blocks.contains(Terrain.EMP)){
                    return DO_NOTHING;
                }else if(myCar.position.lane==1){
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(right.contains(Terrain.EMP)){
                        return TURN_RIGHT;
                    }
                }else if(myCar.position.lane==4){
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.EMP)){
                        return TURN_LEFT;
                    }
                }else{
                    List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block-1, gameState).subList(0,myCar.speed);
                    if(left.contains(Terrain.EMP)){
                        return TURN_LEFT;
                    } else if(right.contains(Terrain.EMP)){
                        return TURN_RIGHT;
                    }
                }
            }
        }
        
        //menghindari rintangan
        if (blocks.contains(Terrain.MUD) || blocks.contains(Terrain.OIL_SPILL) || blocks.contains(Terrain.WALL)) {
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                return LIZARD;
            }
            if (myCar.position.lane==1){ //kalau di lintasan paling kiri, mau ngga mau harus belok kanan
                return TURN_RIGHT;
            } else if (myCar.position.lane==4){ //kalau di lintasan paling kanan, mau ngga mau harus belok kiri
                return TURN_LEFT;
            } else { //kalau bisa milih kanan atau kiri, pilih yang bebas rintangan / rintangan yang menyebabkan kerugian paling kecil
                List<Object> left = getBlocksInFront(myCar.position.lane-1, myCar.position.block, gameState);
                List<Object> right = getBlocksInFront(myCar.position.lane+1, myCar.position.block, gameState);

                if(!left.contains(Terrain.MUD) && !left.contains(Terrain.OIL_SPILL) && !left.contains(Terrain.WALL)){
                    return TURN_LEFT; //gaharus menghindar !!!
                }else if(!right.contains(Terrain.MUD) && !right.contains(Terrain.OIL_SPILL) && !right.contains(Terrain.WALL)){
                    return TURN_RIGHT;
                }else{
                    return TURN_RIGHT; //perbaiki lagi, hitung mana kerusakan yang paling minimum
                }
            }
        }
        //pakai EMP
        if (hasPowerUp(PowerUps.EMP, myCar.powerups) && Math.abs(myCar.position.lane-opponent.position.lane)<=1 && myCar.position.block<opponent.position.block){
            return EMP;   
        }
        //pakai TWEET
        if (hasPowerUp(PowerUps.TWEET, myCar.powerups)){
            return new TweetCommand(opponent.position.lane, opponent.position.block+opponent.speed+1);  //ubah penempatan cybertruck di sini 
        }       
        //pakai OIL
        if (hasPowerUp(PowerUps.OIL, myCar.powerups) && myCar.position.block > opponent.position.block && myCar.position.block-opponent.position.block<Bot.maxSpeed){
            return OIL;
        }
        //perbaikan opsional agar boost / accelerate tidak sia sia
        if (myCar.damage > 1) {
            return FIX;
        }
        //pakai boost
        if(hasPowerUp(PowerUps.BOOST, myCar.powerups) && !(blocksIfBoost.contains(Terrain.MUD) || blocksIfBoost.contains(Terrain.OIL_SPILL) || blocksIfAccelerating.contains(Terrain.WALL))){
            return BOOST;
        }
        
        return ACCELERATE;
    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp: available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns
     * the amount of blocks that can be traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;
        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + 19; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

    private int countPowerups(PowerUps[] available, PowerUps powerups){
        int count=0;
        for(PowerUps x : available){
            if(x.equals(powerups)){
                count=count+1;
            }
        }
        return count;
    }

    private int nextSpeedState(int currentSpeed,int damage){
        if(currentSpeed==0){
            if(damage==5){
                return 0;
            }else{
                return 3;
            }
        } else if(currentSpeed==3){
            if(damage==4){
                return 3;
            }else{
                return 5;
            }
        } else if(currentSpeed==5){
            return 6;
        } else if(currentSpeed==6){
            if(damage==3){
                return 6;
            }else{
                return 8;
            }
        } else if(currentSpeed==8){
            if(damage==2){
                return 8;
            }else{
                return 9;
            }
        }else {
            return 9;
        }
    }

    private int speedIfBoost(int damage){
        if(damage==5){
            return 0;
        }else if(damage==4){
            return 3;
        }else if(damage==3){
            return 6;
        }else if(damage==2){
            return 8;
        }else if(damage==1){
            return 9;
        }else{
            return 15;
        }
    }
}
