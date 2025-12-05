package coursework.poterashki.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticController {

    @GetMapping("/active_announcement")
    public int activeAnnouncement(@RequestParam String id) {
        return 3;
    }

    @GetMapping("/resolved_announcement")
    public int resolvedAnnouncement(@RequestParam String id) {
        return 2;
    }

    @GetMapping("/total_remuneration")
    public int totalRemuneration(@RequestParam String id) {
        return 1201;
    }

    @GetMapping("/max_remuneration")
    public int maxRemuneration(@RequestParam String id) {
        return 3001;
    }

    @GetMapping("/count_responses")
    public int countResponses(@RequestParam String id) {
        return 2;
    }
}