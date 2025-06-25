package com.pettact.api.Category.repository;

import com.pettact.api.Category.entity.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<BoardCategory, Long> {

    boolean existsByBoardCategoryTitle(String boardCategoryTitle);
}
