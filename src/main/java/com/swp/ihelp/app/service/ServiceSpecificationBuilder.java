package com.swp.ihelp.app.service;

import com.swp.ihelp.app.entity.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ServiceSpecificationBuilder {
    private final List<SearchCriteria> params;

    public ServiceSpecificationBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    public ServiceSpecificationBuilder with(String key, String operation, Object value, String orPredicate) {
        params.add(new SearchCriteria(orPredicate, key, operation, value));
        return this;
    }

    public Specification<ServiceEntity> build() {
        if (params.size() == 0) {
            return null;
        }
        Specification<ServiceEntity> result = new ServiceSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i - 1).isOrPredicate()
                    ? Specification.where(result).or(new ServiceSpecification(params.get(i)))
                    : Specification.where(result).and(new ServiceSpecification(params.get(i)));
        }
        return result;
    }
//    public Specification<ServiceEntity> build() {
//        if (params.size() == 0) {
//            return null;
//        }
//
//        List<Specification> specs = params.stream()
//                .map(ServiceSpecification::new)
//                .collect(Collectors.toList());
//
//        Specification result = specs.get(0);
//
//        for (int i = 1; i < params.size(); i++) {
//            result = params.get(i-1).isOrPredicate() ?
//                    Specification.where(result)
//                    .or(specs.get(i)) :
//                    Specification.where(result)
//                    .and(specs.get(i));
//        }
//        return result;
//    }
}
