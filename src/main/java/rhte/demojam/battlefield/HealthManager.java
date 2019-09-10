package rhte.demojam.battlefield;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HealthManager {
    @Value("${BATTLEFIELD_MAX_HEALTH}")
    private int max;

    private AtomicInteger current = new AtomicInteger();

    private String lasthitby;

    @PostConstruct
    private void postConstruct(){
        current.set(max);
    }

    //Decrease health by one. Save the player name if zero health is reached.
    public int decreaseHealth(String hitByPlayer) {
        int health = current.decrementAndGet();
        if (health == 0) lasthitby = hitByPlayer;

        return getHealth();
    }

    //Return non-negative value
    public int getHealth() {
        return Math.max(current.intValue(),0);
    }


    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public AtomicInteger getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current.set(current);
    }

    public String getLasthitby() {
        return lasthitby;
    }

    @Override
    public String toString() {
        return "HealthManager{" +
            "max=" + max +
            ", current=" + current +
            ", lasthitby='" + lasthitby + '\'' +
            '}';
    }
}
