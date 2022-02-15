package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;
import java.lang.Math;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.security.SecureRandom;
import java.util.Arrays;

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

        //jika masih menggunakan boost, prioritaskan strategi menghindari rintangan
        //perbaiki bila rusak darurat
        if ((myCar.damage==4 && myCar.speed==3) || myCar.damage==5) {
            return FIX;
        }
        //jalan terlebih dahulu bila berhenti
        if(myCar.speed==0){
            return ACCELERATE;
        }
        
        //menghitung mana yang paling menguntungkan, belok kiri, belok kanan, atau tetap
        String decision = getMaximumPoint(gameState);
        if(decision=="LEFT"){
            return TURN_LEFT;
        }else if(decision=="RIGHT"){
            return TURN_RIGHT;
        }else if(decision=="LIZARD"){
            return LIZARD;
        }else if(decision=="BOOST"){
            return BOOST;
        }else if(decision=="ACCELERATE"){
            return ACCELERATE;
        }
        //pakai EMP
        if (hasPowerUp(PowerUps.EMP, myCar.powerups) && Math.abs(myCar.position.lane-opponent.position.lane)<=1 && myCar.position.block<opponent.position.block){
            return EMP;   
        }
        //pakai TWEET
        if (hasPowerUp(PowerUps.TWEET, myCar.powerups)){
            return new TweetCommand(opponent.position.lane, opponent.position.block+opponent.speed+3);  //ubah penempatan cybertruck di sini 
        }       
        //pakai OIL
        if (hasPowerUp(PowerUps.OIL, myCar.powerups) && myCar.position.block > opponent.position.block && myCar.position.block-opponent.position.block<Bot.maxSpeed){
            return OIL;
        }
        //perbaikan opsional agar boost / accelerate tidak sia sia
        if ((myCar.damage == 1 && myCar.speed==9) || (myCar.damage == 2 && myCar.speed==8) || (myCar.damage == 3 && myCar.speed==6)) {
            return FIX;
        }
        //pakai boost
        // if(hasPowerUp(PowerUps.BOOST, myCar.powerups) && !(blocksIfBoost.contains(Terrain.MUD) || blocksIfBoost.contains(Terrain.OIL_SPILL) || blocksIfAccelerating.contains(Terrain.WALL))){
        //     return BOOST;
        // }
        
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
        //mode 0 = tidak akselerasi, mode 1 = akselerasi, mode 2 = boost
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

    private int speedIfBoost(int damage) {
        if (damage == 5) {
            return 0;
        } else if (damage == 4) {
            return 3;
        } else if (damage == 3) {
            return 6;
        } else if (damage == 2) {
            return 8;
        } else if (damage == 1) {
            return 9;
        } else {
            return 15;
        }
    }
    
    private int getDamagedMaxSpeed(int damage) {
        if (damage == 5) {
            return 0;
        } else if (damage == 4) {
            return 3;
        } else if (damage == 3) {
            return 6;
        } else if (damage == 2) {
            return 8;
        } else {
            return 9;
        }
    }

    private String getMaximumPoint(GameState gameState){
        int block=gameState.player.position.block;
        int currentSpeed = gameState.player.speed;
        int lane=gameState.player.position.lane;
        List<Lane[]> map = gameState.lanes;
        int startBlock = map.get(0)[0].position.block;
        Lane[] current = getSublane(map.get(lane-1),gameState,currentSpeed);
        int point_current=countPoint(current);
        if(lane==1){
            Lane[] right = getSublane(map.get(lane),gameState,currentSpeed-1);
            int point_right=countPoint(right);
            if(point_right>point_current){
                return "RIGHT";
            }else{
                return getMaximumPointIfStraight(gameState);
            }
        } else if(lane==4){
            Lane[] left = getSublane(map.get(lane-2),gameState,currentSpeed-1);
            int point_left=countPoint(left);
            if(point_left>point_current){
                return "LEFT";
            }else{
                return getMaximumPointIfStraight(gameState);
            }
        }else{
            Lane[] right = getSublane(map.get(lane),gameState,currentSpeed-1);
            Lane[] left = getSublane(map.get(lane-2),gameState,currentSpeed-1);
            int point_left=countPoint(left);
            int point_right=countPoint(right);
            if(point_left>point_current && point_left>point_right){
                return "LEFT";
            }else if(point_right>point_current && point_right>point_left){
                return "RIGHT";
            }else{
                return getMaximumPointIfStraight(gameState);
            }
        }
    }

    private int countPoint(Lane[] lanes){
        int count=0;
        for(Lane lane : lanes){
            if(lane.terrain.equals(Terrain.MUD)){
                count-=2;
            }else if(lane.terrain.equals(Terrain.OIL_SPILL)){
                count-=2;
            }else if(lane.terrain.equals(Terrain.OIL_POWER)){
                count+=1;
            }else if(lane.terrain.equals(Terrain.BOOST)){
                count+=1;
            }else if(lane.terrain.equals(Terrain.WALL)){
                count-=5;
            }else if(lane.terrain.equals(Terrain.LIZARD)){
                count+=1;
            }else if(lane.terrain.equals(Terrain.TWEET)){
                count+=1;
            }else if(lane.terrain.equals(Terrain.EMP)){
                count+=1;
            }else if(lane.isOccupiedByCyberTruck){
                count-=10;
            }
        }
        return count;
    }

    private Lane[] getSublane(Lane[] lanes, GameState gameState, int speed){
        int block = gameState.player.position.block;
        int startBlock = gameState.lanes.get(0)[0].position.block;
        return Arrays.copyOfRange(lanes,max(block - startBlock, 0),min(block-startBlock+speed+1,1500-startBlock));
    }

    private String getMaximumPointIfStraight(GameState gameState){
        int point_nothing=0;
        int point_boost=0;
        int point_accelerate=0;
        int point_lizard=0;
        PowerUps[] available = gameState.player.powerups;
        if(!hasPowerUp(PowerUps.BOOST, available)){
            point_boost-=100;
        }
        if(!hasPowerUp(PowerUps.LIZARD, available)){
            point_lizard-=100;
        }
        int block=gameState.player.position.block;
        int startBlock = gameState.lanes.get(0)[0].position.block;
        int currentSpeed = gameState.player.speed;
        int lane=gameState.player.position.lane;
        List<Lane[]> map = gameState.lanes;
        point_nothing += countPoint(getSublane(map.get(lane - 1), gameState, currentSpeed));
        point_boost += countPowerups(available, PowerUps.BOOST) * 2;
        point_boost += countPoint(getSublane(map.get(lane - 1), gameState, speedIfBoost(gameState.player.damage)));
        point_accelerate -= (gameState.player.speed >= getDamagedMaxSpeed(gameState.player.damage)) ? 10 : 0;
        point_accelerate += countPoint(getSublane(map.get(lane - 1), gameState, nextSpeedState(currentSpeed, gameState.player.damage)));
        try {
            point_lizard+=countPoint(Arrays.copyOfRange(map.get(lane-1),block-startBlock+currentSpeed,block-startBlock+currentSpeed+1));
        } finally{
        
        }
        int max_point = max(max(point_nothing,point_accelerate), max(point_lizard,point_boost));
        if(max_point==point_nothing){
            return "NOTHING";
        }else if(max_point==point_accelerate){
            return "ACCELERATE";
        }else if(max_point==point_lizard){
            return "LIZARD";
        }else{
            return "BOOST";
        }
    }
}
