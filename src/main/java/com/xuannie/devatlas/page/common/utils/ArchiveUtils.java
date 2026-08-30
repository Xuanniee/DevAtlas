package com.xuannie.devatlas.page.common.utils;

import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.page.domain.repository.PageRepository;

import java.time.LocalDateTime;

public class ArchiveUtils {
    public ArchiveUtils() {}

    // Archives a Single Page
    public static void archivePage(Page page, PageRepository pageRepository) {
        page.setArchived(true);
        // Set the timing if archiving else set to null
        page.setArchivedAt(LocalDateTime.now());
        pageRepository.update(page);
    }

    public static void unarchivePage(Page archivedPage, PageRepository pageRepository) {
        // Since the update function only update fields that are not null, we need to reset archived_at in service layer
        archivedPage.setArchived(false);
        pageRepository.update(archivedPage);
    }
}
