package com.bezkoder.springjwt.spec;

import com.bezkoder.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HierarchyService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Return all user IDs that are descendants of the given root userId
     * (including direct children and deeper sub-children).
     */
    public Set<Long> findAllDescendants(Long rootUserId) {
        Set<Long> descendants = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(rootUserId);

        while (!queue.isEmpty()) {
            Long currentId = queue.poll();

            // Find direct children
            List<Long> directChildren = userRepository.findIdsByCreatorId(currentId);
            for (Long childId : directChildren) {
                // Only add if not already in the set, to avoid infinite loops if there's a cycle
                if (!descendants.contains(childId)) {
                    descendants.add(childId);
                    queue.add(childId);
                }
            }
        }

        return descendants;
    }
}
