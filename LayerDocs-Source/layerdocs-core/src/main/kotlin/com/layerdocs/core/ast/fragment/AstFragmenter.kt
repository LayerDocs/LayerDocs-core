package com.layerdocs.core.ast.fragment

import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.layerdocs.block.PageBreak

object AstFragmenter {
    /**
     * Splits a list of nodes into segments based on PageBreak nodes.
     * Each PageBreak node acts as a delimiter and is not included in the segments.
     */
    fun fragment(nodes: List<Node>): List<List<Node>> {
        val fragments = mutableListOf<MutableList<Node>>()
        var currentFragment = mutableListOf<Node>()
        
        for (node in nodes) {
            if (node is PageBreak || node is com.layerdocs.core.ast.base.block.HorizontalRule) {
                // If we encounter a page break or HR, finish the current fragment and start a new one.
                if (currentFragment.isNotEmpty()) {
                    fragments.add(currentFragment)
                    currentFragment = mutableListOf()
                }
            } else {
                currentFragment.add(node)
            }
        }
        
        if (currentFragment.isNotEmpty()) {
            fragments.add(currentFragment)
        }
        
        return fragments
    }
}
