package com.swp.ihelp.app.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RewardServiceImpl implements RewardService{

    private RewardRepository rewardRepository;

    @Autowired
    public RewardServiceImpl(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    @Override
    public List<Reward> findAll() throws Exception {
        return rewardRepository.findAll();
    }

    @Override
    public Reward findById(String id) throws Exception {
        Optional<Reward> result = rewardRepository.findById(id);
        Reward reward = null;
        if (result.isPresent()) {
            reward = result.get();
        }
        return reward;
    }

    @Override
    public List<Reward> findByTitle(String title) throws Exception {
        return rewardRepository.findByTitle(title);
    }

    @Override
    public void save(Reward reward) throws Exception {
        rewardRepository.save(reward);
    }

    @Override
    public void deleteById(String id) throws Exception {
        rewardRepository.deleteById(id);
    }
}
