package syleelsw.anyonesolveit.service.study;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class TagTrieNode {
    private Map<Character, TagTrieNode> children;
    private boolean isEndOfTag;
    private char character;
    private Map<String, Integer> top5Tags;

    public TagTrieNode(char character) {
        this.children = new HashMap<>();
        this.isEndOfTag = false;
        this.character = character;
        this.top5Tags = new HashMap<>();
    }
    public TagTrieNode getChild(char character) {
        if (!children.containsKey(character)) {
            children.put(character, new TagTrieNode(character));
        }
        return children.get(character);
    }
    public void add(String tag, Integer problemCount) {
        if (top5Tags.size() < 5) {
            top5Tags.put(tag, problemCount);
        } else {
            // 가장 작은 값을 찾아서 제거후 추가한다.
            String minKey = "";
            int minValue = Integer.MAX_VALUE;
            for (Map.Entry<String, Integer> entry : top5Tags.entrySet()) {
                if(minValue > entry.getValue()) {
                    minKey = entry.getKey();
                    minValue = entry.getValue();
                }
            }
            if (minValue < problemCount) {
                top5Tags.remove(minKey);
                top5Tags.put(tag, problemCount);
            }
        }
    }
}
