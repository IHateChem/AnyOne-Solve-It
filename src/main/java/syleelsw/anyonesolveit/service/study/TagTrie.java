package syleelsw.anyonesolveit.service.study;

import java.util.List;

public class TagTrie {
    private TagTrieNode root;

    public TagTrie() {
        root = new TagTrieNode(' ');
    }

    public void insert(String tag, Integer problemCount) {
        TagTrieNode current = root;
        for (int i = 0; i < tag.length(); i++) {
            current = current.getChild(tag.charAt(i));
            current.add(tag, problemCount);
        }
    }

    public List<String> search(String tag) {
        TagTrieNode current = root;
        for (int i = 0; i < tag.length(); i++) {
            char ch = tag.charAt(i);
            TagTrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return List.of();
            }
            current = node;
        }
        return current.getTop5Tags().keySet().stream().toList();
    }

}
