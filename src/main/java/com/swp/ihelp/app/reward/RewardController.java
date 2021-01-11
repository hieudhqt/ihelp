package com.swp.ihelp.app.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RewardController {
    private RewardService rewardService;

    @Autowired
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/rewards")
    public List<Reward> findAll() throws Exception {
        return rewardService.findAll();
    }

    @GetMapping("/rewards/{rewardId}")
    public Reward findById(@PathVariable String rewardId) throws Exception {
        return rewardService.findById(rewardId);
    }

    @GetMapping("/rewards/title/{rewardTitle}")
    public List<Reward> findByTitle(@PathVariable String rewardTitle) throws Exception {
        return rewardService.findByTitle(rewardTitle);
    }

    @PostMapping("/rewards")
    public Reward addReward(@RequestBody Reward reward) throws Exception {
        rewardService.save(reward);
        return reward;
    }

    @PutMapping("/rewards")
    public Reward updateReward(@RequestBody Reward reward) throws Exception {
        rewardService.save(reward);
        return reward;
    }

    @DeleteMapping("/rewards/{rewardId}")
    public String deleteReward(@PathVariable String rewardId) throws Exception {
        Reward reward = rewardService.findById(rewardId);
        if (reward == null) {
            throw new RuntimeException("Reward id not found - " + rewardId);
        }
        rewardService.deleteById(rewardId);
        return "Delete Reward with ID: " + rewardId;
    }
}
