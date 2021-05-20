package com.swp.ihelp.app.feedbackcategory;

import java.util.List;

public interface FeedbackCategoryService {

    List<FeedbackCategoryEntity> findAll() throws Exception;

    FeedbackCategoryEntity findById(Integer id) throws Exception;

}
