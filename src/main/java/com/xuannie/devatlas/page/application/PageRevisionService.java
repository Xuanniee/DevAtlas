package com.xuannie.devatlas.page.application;

import com.xuannie.devatlas.page.api.request.CreatePageRevisionRequest;
import com.xuannie.devatlas.page.api.response.PageRevisionResponse;
import com.xuannie.devatlas.page.common.command.CreatePageRevisionCommand;
import com.xuannie.devatlas.page.common.command.RetrievePageRevisionsQuery;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface PageRevisionService {
    PageRevisionResponse create(CreatePageRevisionCommand command);

    List<PageRevisionResponse> findAllByPageId(RetrievePageRevisionsQuery query);
}
