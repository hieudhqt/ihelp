package com.swp.ihelp.app.reward;

import java.util.List;

public interface RewardService {
    List<Reward> findAll() throws Exception;
    Reward findById(String id) throws Exception;
    List<Reward> findByTitle(String title) throws Exception;
    void save(Reward reward) throws Exception;
    void deleteById(String id) throws Exception;
}
