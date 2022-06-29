package com.samic.funcoes.checkboxtree;

import javax.swing.JTree;

/**
 * A tree node user object, for use with a {@link JTree}, that tracks whether it
 * is checked.
 * <p>
 * Thanks to John Zukowski for the <a
 * href="http://www.java2s.com/Code/Java/Swing-JFC/CheckBoxNodeTreeSample.htm"
 * >sample code</a> upon which this is based.
 * </p>
 * 
 * @author Curtis Rueden
 * @see CheckBoxNodeEditor
 * @see CheckBoxNodeRenderer
 */
public class CheckBoxNodeData {

	private String text;
	private boolean checked;
        private String id;
        private String sopcoes;

	public CheckBoxNodeData(final String text, final boolean checked) {
		this.text = text;
		this.checked = checked;
	}

	public CheckBoxNodeData(final String text, final boolean checked, String id, String sopcoes) {
		this.text = text;
		this.checked = checked;
                this.id = id;
                this.sopcoes = sopcoes;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(final boolean checked) {
		this.checked = checked;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSopcoes() {
            return sopcoes;
        }

        public void setSopcoes(String sopcoes) {
            this.sopcoes = sopcoes;
        }
                
	@Override
	public String toString() {
		return getClass().getName() + "[" + text + "/" + checked + "] - id:" + id;
	}

}