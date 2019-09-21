package io.hongstack.eclipse.storyeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class StoryPresentationReconciler extends PresentationReconciler {
    private static final TextAttribute COMMENT_STYLE = new TextAttribute(new Color(Display.getCurrent(), new RGB(63, 127, 95)));
    private static final TextAttribute QUOTE_STYLE =   new TextAttribute(new Color(Display.getCurrent(), new RGB(0, 0, 255)));
    private static final TextAttribute KEYWORD_STYLE = new TextAttribute(new Color(Display.getCurrent(), new RGB(127, 0, 85)), null, SWT.BOLD);
    private static final TextAttribute META_STYLE =    new TextAttribute(new Color(Display.getCurrent(), new RGB(127, 159, 191)), null, SWT.BOLD);

    public StoryPresentationReconciler() {
        RuleBasedScanner scanner= new RuleBasedScanner();
        scanner.setRules(newRules());
        DefaultDamagerRepairer dr= new DefaultDamagerRepairer(scanner);
        this.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        this.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    }

    private IRule[] newRules() {
        List<IRule> rules = new ArrayList<>();
        rules.add(new EndOfLineRule("!--", new Token(COMMENT_STYLE)));
        rules.add(new SingleLineRule("'", "'", new Token(QUOTE_STYLE)));
        rules.add(new SingleLineRule("\"","\"", new Token(QUOTE_STYLE)));
        rules.add(new KeywordRule(new Token(KEYWORD_STYLE)));
        rules.add(new MetaRule(new Token(META_STYLE)));
        return rules.toArray(new IRule[rules.size()]);
    }

    private static class KeywordRule extends WordRule {
        public KeywordRule(Token token) {
            super(new WordDetector());
            String[] keywords = {"Meta", "Narrative", "GivenStories", "Lifecycle", "Before", "After", "Scope", "Outcome", "MetaFilter", "Scenario", "Given", "When", "Then", "And", "Examples"};
            for (String word : keywords) {
                addWord(word, token);
            }
        }
    }

    private static class MetaRule extends WordRule {
        public MetaRule(Token token) {
            super(new WordDetector() {
                @Override
                public boolean isWordStart(char c) {
                    return '@' == c;
                }
            }, token);
        }
    }

    private static class WordDetector implements IWordDetector {
        @Override
        public boolean isWordPart(char c) {
            return Character.isJavaIdentifierPart(c);
        }

        @Override
        public boolean isWordStart(char c) {
            return Character.isJavaIdentifierStart(c);
        }
    }
}