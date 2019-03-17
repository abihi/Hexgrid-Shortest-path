import java.lang.Math;
import java.util.*;

class Cell {
    private Cell NE, NW, SW, SE, W, E;
    private boolean hardWax = false, visited = false;

    private Integer distance = Integer.MAX_VALUE;
    private List<Cell> shortestPath = new LinkedList<>();
    int id;

    Cell(int id){
        this.id = id;
    }

    void setHardWax(){ this.hardWax = true; }
    boolean isHardWax(){ return hardWax; }
    void setDistance(int distance){ this.distance = distance; }
    Integer getDistance(){ return distance; };
    boolean getVisited() { return this.visited; }
    void setVisited() { this.visited = true; }
    void removeVisited() { this.visited = false; }
    void setShortestPath(List<Cell> sp){ this.shortestPath = sp; }
    List<Cell> getShortestPath(){ return shortestPath; }

    void setNeighbors(Cell NE, Cell NW, Cell SW, Cell SE, Cell W, Cell E){
       if(NE != null){
           if( !NE.isHardWax() ){ this.NE = NE; }
       }
       if(NW != null){
           if( !NW.isHardWax() ){ this.NW = NW; }
       }
       if(SW != null) {
           if( !SW.isHardWax() ){ this.SW = SW; }
       }
        if(SE != null){
            if( !SE.isHardWax() ){ this.SE = SE; }
        }
        if(W != null){
            if( !W.isHardWax() ){ this.W = W; }
        }
        if(E != null){
            if( !E.isHardWax() ){ this.E = E; }
        }

    }

    ArrayList<Cell> getNeighbors(){
        ArrayList<Cell> neighbors = new ArrayList<>();
        if(NE != null) {neighbors.add(NE);}
        if(NW != null) {neighbors.add(NW);}
        if(SW != null) {neighbors.add(SW);}
        if(SE != null) {neighbors.add(SE);}
        if(E != null) {neighbors.add(E);}
        if(W != null) {neighbors.add(W);}
        return neighbors;
    }

    @Override
    public String toString(){
        String neID = (NE == null) ? "Empty" : String.valueOf(NE.id);
        String nwID = (NW == null) ? "Empty" : String.valueOf(NW.id);
        String swID = (SW == null) ? "Empty" : String.valueOf(SW.id);
        String seID = (SE == null) ? "Empty" : String.valueOf(SE.id);
        String wID  = (W == null) ? "Empty" : String.valueOf(W.id);
        String eID  = (E == null) ? "Empty" : String.valueOf(E.id);
        //return "Id: " + id + " distance: " + distance;
        return "Id: " + id + " NW: " + nwID + " NE: " + neID  + " SW: " + swID
                + " SE: " + seID + " W: " + wID + " E: " + eID + " hardWax: " + hardWax
                + " distance: " + distance;
    }
}

class HoneyComb {
    private HashMap<Integer, Cell> honeyComb;
    HoneyComb(){
        this.honeyComb = new HashMap<>();
    }

    void createCells(int total_cells){
        for (int i = 1; i <= total_cells; i++){
            honeyComb.put(i, new Cell(i));
        }
    }

    void linkCellsToNeighbors(int[][] init_board , int R){
        for (int x = 1; x < init_board.length; x++) {
            for (int y = 1; y < init_board[x].length; y++) {
                if(init_board[x][y] > 0) {
                    Cell cell = honeyComb.get(init_board[x][y]);
                    Cell NE, NW, SW, SE, W, E;
                    if(x<R+1){ // Upper part
                        NW = honeyComb.get(init_board[x-1][y-1]);
                        NE = honeyComb.get(init_board[x-1][y]);
                        W = honeyComb.get(init_board[x][y-1]);
                        E = honeyComb.get(init_board[x][y+1]);
                        SW = honeyComb.get(init_board[x+1][y]);
                        SE = honeyComb.get(init_board[x+1][y+1]);
                    } else if(x==R+1){ // Middle part
                        NW = honeyComb.get(init_board[x-1][y-1]);
                        NE = honeyComb.get(init_board[x-1][y]);
                        W = honeyComb.get(init_board[x][y-1]);
                        E = honeyComb.get(init_board[x][y+1]);
                        SW = honeyComb.get(init_board[x+1][y-1]);
                        SE = honeyComb.get(init_board[x+1][y]);
                    } else { // Lower part
                        NW = honeyComb.get(init_board[x-1][y]);
                        NE = honeyComb.get(init_board[x-1][y+1]);
                        W = honeyComb.get(init_board[x][y-1]);
                        E = honeyComb.get(init_board[x][y+1]);
                        SW = honeyComb.get(init_board[x+1][y-1]);
                        SE = honeyComb.get(init_board[x+1][y]);
                    }

                    cell.setNeighbors(NE, NW, SW, SE, W, E);
                }

            }
        }
    }

    void defineHardenedCells(int X, ArrayList<Integer> ids){
        for(Integer id : ids){
            Cell cell = honeyComb.get(id);
            cell.setHardWax();
        }
    }

    Cell getCell(int id){
        return honeyComb.get(id);
    }

    void printAllCells(){
        for(Cell cell : honeyComb.values()){
            System.out.println(cell);
        }
    }

    Cell getUnvisitedChildNode(Cell cell){
        ArrayList<Cell> neighbors = cell.getNeighbors();
        for(Cell neighborCell : neighbors){
            if(!neighborCell.getVisited()){
                return neighborCell;
            }
        }
        return null;
    }

    void clearVisitedCells(){
        for(Cell cell : honeyComb.values()){
            cell.removeVisited();
        }
    }

    int bfs(int start, int goal){
        Cell rootCell = honeyComb.get(start);
        rootCell.setDistance(0);

        if(rootCell.isHardWax()){
            return Integer.MAX_VALUE;
        }

        Set<Cell> settledNodes = new HashSet<>();
        Set<Cell> unsettledNodes = new HashSet<>();

        unsettledNodes.add(rootCell);

        while(unsettledNodes.size() != 0) {
            Cell currentCell = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentCell);
            ArrayList<Cell> neighbors = currentCell.getNeighbors();
            for(Cell neighborCell : neighbors){
                if(!settledNodes.contains(neighborCell)){
                    calculateMinimumDistance(neighborCell, currentCell);
                    unsettledNodes.add(neighborCell);
                }
            }
            settledNodes.add(currentCell);
        }

        return honeyComb.get(goal).getShortestPath().size();
    }

    private Cell getLowestDistanceNode(Set<Cell> unsettledNodes) {
        Cell lowestDistanceCell = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Cell cell: unsettledNodes) {
            int cellDistance = cell.getDistance();
            if (cellDistance < lowestDistance) {
                lowestDistance = cellDistance;
                lowestDistanceCell = cell;
            }
        }
        return lowestDistanceCell;
    }

    private void calculateMinimumDistance(Cell evalCell, Cell rootCell) {
        Integer rootDistance = rootCell.getDistance();
        if (rootDistance + 1 < evalCell.getDistance()) {
            evalCell.setDistance(rootDistance + 1);
            LinkedList<Cell> shortestPath = new LinkedList<>(rootCell.getShortestPath());
            shortestPath.add(rootCell);
            evalCell.setShortestPath(shortestPath);
        }
    }
}

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // length of grid edges
        int R = sc.nextInt();
        // Maximum number of steps
        int N = sc.nextInt();
        // start
        int A = sc.nextInt();
        // goal
        int B = sc.nextInt();
        // No. harden
        int X = sc.nextInt();
        ArrayList<Integer> hardenIds = new ArrayList<>();
        while(sc.hasNext()){
            hardenIds.add(sc.nextInt());
        }

        int total_cells = (int) (Math.pow(R, 3) - Math.pow(R-1, 3));
        //System.out.println("Total cells: " + total_cells);
        int[][] init_board = new int[R*2+2][R*2+2];

        int n = 1;
        int rowIt = 1;
        boolean reachedMid = false;
        for (int i = 2; i < init_board.length-1; i++) {
            int rowLen = (R + rowIt);
            if( R+rowIt < R*2 && !reachedMid ){
                rowIt++;
            } else{ reachedMid = true; }
            if(reachedMid){ rowIt--; }

            for (int j = 2; j < rowLen+1; j++) {
                init_board[i][j] = n;
                n++;
            }
        }

        for (int i = 1; i < init_board.length; i++) {
            for(int j = 1; j < init_board[i].length; j++){
                System.out.print(init_board[i][j] + "\t");
            }
            System.out.println();
        }

        HoneyComb honeyComb = new HoneyComb();
        honeyComb.createCells(total_cells);
        honeyComb.defineHardenedCells(X, hardenIds);
        honeyComb.linkCellsToNeighbors(init_board, R);
        honeyComb.printAllCells();
        int shortestPath = honeyComb.bfs(A, B);

        //System.out.println(honeyComb.getCell(B).getShortestPath());

        if(shortestPath <= N) {
            System.out.println(shortestPath);
        } else {
            System.out.println("No");
        }

    }
}
