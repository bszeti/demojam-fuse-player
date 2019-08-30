package rhte.demojam.battlefield;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Battlefield {

    @Value("#{'${BATTLEFIELD_PLAYER_URLS}'.split(',')}")
    List<String> urls;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
