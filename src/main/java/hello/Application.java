package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
public class Application {

    static class Self {
        public String href;
    }

    static class Links {
        public Self self;
    }

    static class PlayerState {
        public Integer x;
        public Integer y;
        public String direction;
        public Boolean wasHit;
        public Integer score;
    }

    static class Arena {
        public List<Integer> dims;
        public Map<String, PlayerState> state;
    }

    static class ArenaUpdate {
        public Links _links;
        public Arena arena;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

    @GetMapping("/")
    public String index() {
        return "Let the battle begin!";
    }

    public Integer MY_X = null;
    public Integer MY_Y = null;
    public String MY_DIRECTION = null;

    @PostMapping("/**")
    public String index(@RequestBody ArenaUpdate arenaUpdate) {
        System.out.println(arenaUpdate);
        Map<String, PlayerState> plyers = arenaUpdate.arena.state;
        PlayerState myState = arenaUpdate.arena.state.get(arenaUpdate._links.self.href);
        MY_X = myState.x;
        MY_Y = myState.y;
        MY_DIRECTION = myState.direction;

        if (myState.wasHit) {
            List<PlayerState> aheadPlayers = aheadPlayers(MY_DIRECTION, plyers);
            if (aheadPlayers.size() != 0) {
                if (gotStock(aheadPlayers) || someoneFaceMe(aheadPlayers)) {
                    return "T";
                } else {
                    return "F";
                }
            } else {
                return "F";
            }
        }
        if (aheadPlayers(MY_DIRECTION, plyers).size() != 0)
            return "T";

        String[] L_R = getLR(MY_DIRECTION);
        int L_players = aheadPlayers(L_R[0], plyers).size();
        int R_players = aheadPlayers(L_R[1], plyers).size();
        if (R_players >= L_players) {
            return "R";
        } else {
            return "L";
        }
    }

    private boolean someoneFaceMe(List<PlayerState> aheadPlayers) {
        for (PlayerState player : aheadPlayers) {
            if (MY_DIRECTION.equals("N") && player.direction.equals("S"))
                return true;
            if (MY_DIRECTION.equals("S") && player.direction.equals("N"))
                return true;
            if (MY_DIRECTION.equals("W") && player.direction.equals("E"))
                return true;
            if (MY_DIRECTION.equals("E") && player.direction.equals("W"))
                return true;
        }
        return false;
    }

    private boolean gotStock(List<PlayerState> aheadPlayers) {
        for (PlayerState player : aheadPlayers) {
            if (MY_DIRECTION.equals("N") && MY_Y - 1 == player.y)
                return true;
            if (MY_DIRECTION.equals("S") && MY_Y + 1 == player.y)
                return true;
            if (MY_DIRECTION.equals("W") && MY_X - 1 == player.x)
                return true;
            if (MY_DIRECTION.equals("E") && MY_X + 1 == player.x)
                return true;
        }
        return false;
    }

    private String[] getLR(String my_direction) {
        if (my_direction.equals("N"))
            return new String[]{"W", "E"};
        if (my_direction.equals("S"))
            return new String[]{"E", "W"};
        if (my_direction.equals("W"))
            return new String[]{"S", "N"};
        if (my_direction.equals("E"))
            return new String[]{"N", "S"};
        return new String[2];
    }

    private List<PlayerState> aheadPlayers(String direction, Map<String, PlayerState> plyers) {
        List<PlayerState> aheadInfo = new ArrayList<>();
        for (Map.Entry<String, PlayerState> entry : plyers.entrySet()) {
            PlayerState state = entry.getValue();
            if (direction.equals("N") && state.x.equals(MY_X) && (MY_Y - 1 >= state.y && MY_Y - 3 <= state.y))
                aheadInfo.add(state);
            if (direction.equals("S") && state.x.equals(MY_X) && (MY_Y + 1 <= state.y && MY_Y + 3 >= state.y))
                aheadInfo.add(state);
            if (direction.equals("W") && state.y.equals(MY_Y) && (MY_X - 1 >= state.x && MY_X - 3 <= state.x))
                aheadInfo.add(state);
            if (direction.equals("E") && state.y.equals(MY_Y) && (MY_X + 1 <= state.x && MY_X + 3 >= state.x))
                aheadInfo.add(state);
        }
        return aheadInfo;
    }
}

