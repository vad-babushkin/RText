/*
 * 11/05/2009
 *
 * Tool.java - An "external tool."
 * Copyright (C) 2009 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://rtext.fifesoft.com
 *
 * This file is a part of RText.
 *
 * RText is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * RText is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.fife.rtext.plugins.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fife.io.ProcessRunner;
import org.fife.io.ProcessRunnerOutputListener;


/**
 * An "external tool."
 *
 * @author Robert Futrell
 * @version 1.0
 */
/*
 * NOTE: In 1.5, most of these fields could be replaced with a single
 * ProcessBuilder instance.
 */
public class Tool implements Comparable {

	private String name;
	private String desc;
	private String dir; // Not File, to ease serialization.
	private String program;
	private List args;
	private Map env;
	private boolean appendEnv;
	private String accelerator; // String to ease serialization


	/**
	 * Constructor.  This is really only here to make this class a JavaBean
	 * to facilitate easy serializing; the {@link #Tool(String, String)}
	 * constructor is preferred over this one.
	 */
	public Tool() {
		init();
	}


	/**
	 * Constructor.
	 *
	 * @param name The name of this tool.
	 * @param desc A description of this tool.  This may be <code>null</code>.
	 */
	public Tool(String name, String desc) {
		setName(name);
		setDescription(desc);
		init();
	}


	/**
	 * Adds a command line argument for this tool.
	 *
	 * @param arg The argument.  This cannot be <code>null</code>.
	 * @see #clearArgs()
	 * @see #setProgram(String)
	 */
	public void addArg(String arg) {
		if (arg==null) {
			throw new IllegalArgumentException("arg cannot be null");
		}
		args.add(arg);
	}


	/**
	 * Clears the command line arguments.
	 *
	 * @see #addArg(String)
	 */
	public void clearArgs() {
		args.clear();
	}


	/**
	 * Clears the environment variables associated with this tool.
	 * Note that if this tool is appending its environment to RText's
	 * environment, this does not clear the RText environment that is
	 * appended to; it only clears the environment variables to add.
	 *
	 * @see #putEnvVar(String, String)
	 */
	public void clearEnvVars() {
		env.clear();
	}


	/**
	 * Compares this tool to another by name, lexicographically.
	 *
	 * @param o The other tool.
	 * @return The sort order of this tool, compared to another.
	 */
	public int compareTo(Object o) {
		int val = -1;
		if (o==this) {
			val = 0;
		}
		else if (o instanceof Tool) {
			val = getName().compareTo(((Tool)o).getName());
		}
		return val;
	}


	/**
	 * Returns whether this tool and another have the same name.
	 *
	 * @return Whether this tool and another have the same name.
	 */
	public boolean equals(Object o) {
		return compareTo(o)==0;
	}


	/**
	 * Runs this tool in a separate thread.
	 *
	 * @param l Listens for events as this tool runs.
	 */
	public void execute(final ProcessRunnerOutputListener l) {

		final String[] cmd = new String[1 + args.size()];
		cmd[0] = program;
		for (int i=0; i<args.size(); i++) {
			cmd[i+1] = (String)args.get(i);
		}

		Thread t = new Thread() {
			public void run() {
				ProcessRunner pr = new ProcessRunner(cmd);
				pr.setDirectory(new File(getDirectory()));
				pr.setEnvironmentVars(env, appendEnv);
				pr.setOutputListener(l);
				pr.run();
			}
		};

		t.start();

	}


	/**
	 * Returns the accelerator to use to activate this tool in a menu.
	 *
	 * @return The accelerator, or <code>null</code> if there is none.
	 * @see #setAccelerator(String)
	 */
	public String getAccelerator() {
		return accelerator;
	}


	/**
	 * Returns whether this tool should append any environment variables
	 * it defines to RText's current environment.
	 *
	 * @return Whether to append the environment variables defined.  If this
	 *         value is <code>false</code>, RText's environment is not
	 *         appended.
	 * @see #setAppendEnvironmentVars(boolean)
	 */
	public boolean getAppendEnvironmentVars() {
		return appendEnv;
	}


	/**
	 * Returns the command line arguments for this Tool, as an array.
	 *
	 * @return An array of command line arguments, or an empty array if there
	 *         are none.
	 * @see #setArgs(String[])
	 */
	public String[] getArgs() {
		String[] args = new String[this.args.size()];
		return (String[])this.args.toArray(args);
	}


	/**
	 * Returns a description of this tool.
	 *
	 * @return A description of this tool, or <code>null</code> if none
	 *         is defined.
	 * @see #setDescription(String)
	 */
	public String getDescription() {
		return desc;
	}


	/**
	 * Returns a copy of the environment variable map for this tool.
	 *
	 * @return The environment variables.  This is a <code>Map</code> with both
	 *         keys and values as Strings.
	 * @see #setEnvVars(Map)
	 */
	public Map getEnvVars() {
		HashMap vars = new HashMap();
		vars.putAll(env);
		return vars;
	}


	/**
	 * Returns the name of this tool.
	 *
	 * @return The name of this tool.
	 * @see #setName(String)
	 */
	public String getName() {
		return name;
	}


	/**
	 * Returns the program to launch.
	 *
	 * @return The program to launch.
	 * @see #setProgram(String)
	 */
	public String getProgram() {
		return program;
	}


	/**
	 * Returns the directory the tool will run in.
	 *
	 * @return The directory.
	 * @see #setDirectory(String)
	 */
	public String getDirectory() {
		return dir;
	}


	/**
	 * Returns the hash code of this tool.
	 *
	 * @return This tool's hash code.
	 */
	public int hashCode() {
		return getName().hashCode();
	}


	/**
	 * Initializes this tool.
	 */
	private void init() {
		args = new ArrayList(3);
		env = new HashMap();
	}


	/**
	 * Sets an environment variable for this tool.
	 *
	 * @param name The name of the environment variable.
	 * @param value The value of the variable.  If this is <code>null</code>,
	 *        then this variable will not be set with a special value.
	 * @see #clearEnvVars()
	 */
	public void putEnvVar(String name, String value) {
		// env.put(name, null) will store a null value into a HashMap
		if (value!=null) {
			env.put(name, value);
		}
		else {
			env.remove(name);
		}
	}


	/**
	 * Sets the accelerator to use to activate this tool in a menu.
	 *
	 * @param accelerator The accelerator to use, or <code>null</code> for
	 *        none.
	 * @see #getAccelerator()
	 */
	public void setAccelerator(String accelerator) {
		this.accelerator = accelerator;
	}


	/**
	 * Sets whether this tool should append any environment variables
	 * it defines to RText's current environment.
	 *
	 * @param append Whether to append the environment variables defined.  If
	 *        this value is <code>false</code>, RText's environment is not
	 *        appended.
	 * @see #getAppendEnvironmentVars()
	 */
	public void setAppendEnvironmentVars(boolean append) {
		this.appendEnv = append;
	}


	/**
	 * Sets the command line arguments to this tool.  Old command line arguments
	 * are discarded.
	 *
	 * @param args The new command line arguments.
	 * @see #getArgs()
	 */
	public void setArgs(String[] args) {
		clearArgs();
		if (args!=null) {
			for (int i=0; i<args.length; i++) {
				addArg(args[i]);
			}
		}
	}


	/**
	 * Sets a description of this tool.
	 *
	 * @param desc A description of this tool.  This may be <code>null</code>.
	 * @see #getDescription()
	 */
	public void setDescription(String desc) {
		this.desc = desc;
	}


	/**
	 * Sets the directory for this tool to run in.
	 *
	 * @param dir The directory.  This cannot be <code>null</code>.
	 * @see #getDirectory()
	 */
	public void setDirectory(String dir) {
		if (dir==null) {
			throw new IllegalArgumentException("dir cannot be null");
		}
		this.dir = dir;
	}


	/**
	 * Sets the environment variables for this tool.
	 *
	 * @param vars A String-to-String mapping of environment variables.
	 * @see #getEnvVars()
	 */
	public void setEnvVars(Map vars) {
		env.clear();
		env.putAll(vars);
	}


	/**
	 * Sets the name of this tool.
	 *
	 * @param name The name of this tool.
	 * @see #getName()
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Sets the program to launch.
	 *
	 * @param program The program.  This cannot be <code>null</code>.
	 * @see #getProgram()
	 * @see #addArg(String)
	 */
	public void setProgram(String program) {
		if (program==null) {
			throw new IllegalArgumentException("program cannot be null");
		}
		this.program = program;
	}


	public static void main(String[] args) {
		Tool tool = new Tool("Name", "Desc");
		tool.setProgram("C:/temp/test.bat");
		tool.execute(new ProcessRunnerOutputListener() {
			public void outputWritten(String output, boolean stdout) {
				System.out.println(output);
			}
		});
	}
}