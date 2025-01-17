package program;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import include.GUI;
import include.GUI.Button;
import include.GUI.CButListener;
import include.GUI.CButton;
import include.GUI.Container;
import include.GUI.Image;
import include.GUI.Label;
import include.GUI.TextArea;
import include.GUI.TextField;
import include.GUI.WinLis;
import include.JsonParser;
import include.NEF;

public class MainFrame {

	private static GUI gui = null;
	
	private static Hashtable<String, Run> profiles = new Hashtable<>();
	
	private static void add(String name, Run run) {
		profiles.put(name, run);
		addProfile(name);
	}
	
	private static void remove(String name) {
		try {
			profiles.get(name).setRunning(false);
		} catch (Exception e) {}
		
		profiles.remove(name);
	}

	public static void open() {
		
		
		gui = new GUI("StreamRaider Bot " + StreamRaiders.get("botVersion"), 500, 700);
		
		gui.addWinLis(new WinLis() {
			@Override
			public void onIconfied(WindowEvent e) {}
			@Override
			public void onFocusLost(WindowEvent e) {}
			@Override
			public void onFocusGained(WindowEvent e) {}
			@Override
			public void onDeIconfied(WindowEvent e) {}
			@Override
			public void onClose(WindowEvent e) {
				close();
			}
		});
		
		Container head = new Container();
		head.setPos(0, 0);
		head.setFill('h');
		
			Button nextAll = new Button();
			nextAll.setPos(0, 0);
			nextAll.setText("\u23E9");
			nextAll.setFont(new Font(null, Font.PLAIN, 20));
			nextAll.setAL(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for(String key : profiles.keySet()) {
						try {
							profiles.get(key).interrupt();
						} catch (Exception e1) {}
					}
				}
			});
			head.addBut(nextAll);
		
			Label s1 = new Label();
			s1.setPos(1, 0);
			s1.setText("");
			s1.setWeightX(1);
			head.addLabel(s1);
			
			Button refresh = new Button();
			refresh.setPos(2, 0);
			refresh.setText("\u27F3");
			refresh.setFont(new Font(null, Font.PLAIN, 20));
			refresh.setAL(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					refresh();
				}
			});
			head.addBut(refresh);
			
			Button addPro = new Button();
			addPro.setPos(3, 0);
			addPro.setText("+");
			addPro.setFont(new Font(null, Font.PLAIN, 20));
			addPro.setAL(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GUI np = new GUI("New Profile", 300, 400);
					
					
					Label lab1 = new Label();
					lab1.setPos(0, 0);
					lab1.setText("Profilename");
					np.addLabel(lab1);
					
					ActionListener openBrowser = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String in = GUI.getInputText("newName");
							if(in.equals("")) {
								if(!np.showConfirmationBox("go ahead without a name for the profile?")) {
									return;
								}
							}
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										new Browser(in);
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							});
							t.start();
							np.close();
						}
					};
					
					TextField name = new TextField();
					name.setPos(0, 1);
					name.setText("");
					name.setFill('h');
					name.setAL(openBrowser);
					np.addTextField(name, "newName");
					
					Button open = new Button();
					open.setPos(0, 2);
					open.setText("open Browser to Login");
					open.setAL(openBrowser);
					np.addBut(open);
					
				}
			});
			head.addBut(addPro);
			
			Button opt = new Button();
			opt.setPos(4, 0);
			opt.setText("\u23E3");
			opt.setFont(new Font(null, Font.PLAIN, 20));
			opt.setAL(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GUI opt = new GUI("Options", 400, 500);
					
					Button fm = new Button();
					fm.setPos(0, 0);
					fm.setText("forget me");
					fm.setAL(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							forgetMe();
						}
					});
					opt.addBut(fm);
					
					Label s1 = new Label();
					s1.setPos(0, 1);
					s1.setSize(1, 20);
					s1.setText("");
					opt.addLabel(s1);
					
					TextArea don = new TextArea();
					don.setPos(0, 2);
					don.setEditable(false);
					don.setText("Donations:\n"
							+ "Bitcoin:\n"
							+ "3FUVTkAijeuNgDyvrvvcGiAXhbxGhyCGBR\n"
							+ "\n"
							+ "Litecoin:\n"
							+ "MP8Y6X6irqarK8KpL6LkWnZpW4LZgGZgWU\n"
							+ "\n"
							+ "Ethereum:\n"
							+ "0x0c9a44a9b6388f030d91dec2ed50b6d3139418a1\n"
							+ "\n"
							+ "Monero:\n"
							+ "49Jk21tDSxsHGvzK7JrMu5UxrKhvkXu3xCXsrrNyqGJc1Kus27PUHZDDSK13fCQL8S7BcokBM3tbX7fwg1cFt6QeE3ycaYT\n"
					);
					opt.addTextArea(don);
				}
			});
			head.addBut(opt);
			
			gui.addContainer(head);
			
			refresh();
	}
	
	private static void forgetMe() {
		if(gui.showConfirmationBox("do you really want\nto delete all your\nprofiles?")) {
			Set<String> kset = profiles.keySet();
			String[] keys = new String[kset.size()];
			keys = kset.toArray(keys);
			for(String key : keys) {
				forget(key);
			}
		}
	}
	
	private static void forget(String name) {
		File pro = new File("profiles/" + name + ".app");
		File con = new File("configs/" + name + ".app");
		try {
			pro.delete();
			con.delete();
			gui.remove(name + "::part");
			remove(name);
		} catch (Exception e) {
			gui.msg("Error", "Error while trying to forget:\n" + name, GUI.MsgConst.ERROR);
			e.printStackTrace();
		}
	}
	
	private static int pos = 1;
	
	private static Hashtable<String, Hashtable<String, String>> blacks = new Hashtable<>();
	
	public static Hashtable<String, String> getBlacklist(String name) {
		return blacks.get(name);
	}
	
	private static void addProfile(String name) {
		try {
			blacks.put(name, NEF.getOpt("configs/" + name + ".app"));
		} catch (Exception e) {
			Hashtable<String, String> def = new Hashtable<>();
			
			JsonObject types = Unit.getTypes();
			
			for(String type : types.keySet()) {
				if(types.getAsJsonObject(type).getAsJsonPrimitive("rank").getAsInt() != 4) {
					def.put(type, "true");
					def.put("uid_" + type, "null");
				} else {
					def.put(type, "false");
					def.put("uid_" + type, "null");
				}
			}
			
			blacks.put(name, def);
		}
		
		Container both = new Container();
		both.setPos(0, pos++);
		both.setFill('h');
		both.setBorder(Color.lightGray, 1, 30);
		both.setInsets(5, 0, 5, 0);
		
		Container top = new Container();
		top.setFill('h');
		top.setPos(0, 0);
		top.setWeightX(1);
		
		Label profileName = new Label();
		profileName.setPos(0, 0);
		profileName.setText(name);
		top.addLabel(profileName);
		
		Label s2 = new Label();
		s2.setPos(1, 0);
		s2.setSize(20, 1);
		s2.setText("");
		top.addLabel(s2);
		
		Label counter = new Label();
		counter.setPos(2, 0);
		counter.setText("");
		top.addLabel(counter, name + "::counter");
		
		Label s3 = new Label();
		s3.setPos(3, 0);
		s3.setText("");
		s3.setWeightX(1);
		top.addLabel(s3);
		
		both.addContainer(top);
		
		Container part = new Container();
		part.setFill('h');
		part.setPos(0, 1);
		part.setWeightX(1);
		
		CButton start = new CButton(name+"::start");
		start.setPos(0, 0);
		start.setSpan(1, 4);
		start.setFill('v');
		start.setText("\u23F5");
		start.setFont(new Font(null, Font.PLAIN, 20));
		start.setCBL(new CButListener() {
			@Override
			public void unselected(String id, ActionEvent e) {
				GUI.setText(id, "\u23F5");
				GUI.setBackground(id, GUI.getDefButCol());
				profiles.get(name).setRunning(false);
			}
			@Override
			public void selected(String id, ActionEvent e) {
				GUI.setText(id, "\u23F8");
				GUI.setBackground(id, Color.green);
				profiles.get(name).setRunning(true);
			}
		});
		part.addCButton(start);
		
		Button next = new Button();
		next.setPos(1, 0);
		next.setSpan(1, 4);
		next.setFill('v');
		next.setText("\u23E9");
		next.setFont(new Font(null, Font.PLAIN, 20));
		next.setAL(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				profiles.get(name).interrupt();
			}
		});
		part.addBut(next);
		
		
		for(int i=0; i<4; i++) {
			Label name1 = new Label();
			name1.setText(""+(i+1));
			name1.setPos(2, i);
			part.addLabel(name1, name+"::name::"+i);
			
			final int ii = i;
			Button map = new Button();
			map.setPos(3, i);
			map.setText("Map");
			map.setAL(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					profiles.get(name).showMap(ii);
				}
			});
			part.addBut(map);
		}
		
		Label s1 = new Label();
		s1.setPos(4, 0);
		s1.setSpan(1, 4);
		s1.setText("");
		s1.setWeightX(1);
		s1.setFill('h');
		part.addLabel(s1);
		
		Button seeRews = new Button();
		seeRews.setPos(5, 0);
		seeRews.setSpan(1, 4);
		seeRews.setFill('v');
		seeRews.setText("\u26C1");
		seeRews.setFont(new Font(null, Font.PLAIN, 20));
		seeRews.setAL(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JsonObject rews = profiles.get(name).getRews();
				
				String text = "";
				Set<String> keys = rews.keySet();
				
				for(String key : keys) {
					if(!key.contains("chest")) continue;
					text += "|" + key + " " + rews.getAsJsonPrimitive(key).getAsString() + "\n";
				}
				
				for(int i=0; i<rewBefore.length; i++) {
					try {
						text += "|" + rewBefore[i] + " " + rews.getAsJsonPrimitive(rewBefore[i]).getAsString() + "\n";
					} catch (Exception e1) {}
				}
				
				for(String key: keys) {
					if(Arrays.asList(rewBefore).indexOf(key) != -1 || key.contains("chest")) continue;
					text += "|" + key + " " + rews.getAsJsonPrimitive(key).getAsString() + "\n";
				}
				
				GUI gui = new GUI("Rewards for " + name, 300, 300);
				
				TextArea ta = new TextArea();
				ta.setEditable(false);
				ta.setText(text);
				ta.setPos(0, 0);
				gui.addTextArea(ta);
				
			}
		});
		part.addBut(seeRews);
		
		Button stngs = new Button();
		stngs.setPos(6, 0);
		stngs.setSpan(1, 4);
		stngs.setFill('v');
		stngs.setText("\u23E3");
		stngs.setFont(new Font(null, Font.PLAIN, 20));
		stngs.setAL(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUI gui = new GUI("Profile Settings", 900, 800);
				
				JsonObject types = Unit.getTypes();
				Hashtable<String, String> black = blacks.get(name);
				
				Container units = new Container();
				units.setPos(0, 0);
				
				int x = 0;
				int y = 1;
				for(final String type : types.keySet()) {
					
					int rank = types.getAsJsonObject(type).getAsJsonPrimitive("rank").getAsInt();
					if(rank != y) {
						y = rank;
						x = 0;
					}
					
					Container unit = new Container();
					unit.setPos(x++, y);
					
					Container cimg = new Container();
					Image img = new Image("data/UnitPics/" + type + ".gif");
					img.setSquare(100);
					cimg.addImage(img);
					
					Button c = new Button();
					c.setPos(0, 0);
					c.setContainer(cimg);
					c.setFill('h');
					if(Boolean.parseBoolean(black.get(type))) {
						c.setBackground(Color.green);
					} else {
						c.setBackground(GUI.getDefButCol());
					}
					
					c.setAL(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if(Boolean.parseBoolean(black.get(type))) {
								blacks.get(name).put(type, "false");
								GUI.setBackground(name + "::" + type, GUI.getDefButCol());
							} else {
								blacks.get(name).put(type, "true");
								GUI.setBackground(name + "::" + type, Color.green);
							}
						}
					});
					unit.addBut(c, name + "::" + type);
					
					Button spec = new Button();
					spec.setPos(0, 1);
					spec.setText("\u23E3 specialize");
					spec.setFont(new Font(null, Font.PLAIN, 19));
					spec.setFill('h');
					spec.setAL(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							final JsonArray uids = JsonParser.json(StreamRaiders.get("specUIDs")).getAsJsonArray(type);
							
							String old = blacks.get(name).get("uid_" + type);
							
							GUI gspec = new GUI("specialize " + type, 400, 300);
							
							
							for(int i=0; i<3; i++) {
								final String u = uids.get(i).getAsJsonObject().getAsJsonPrimitive("uid").getAsString();
								final String uName = uids.get(i).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
								final int ii = i;
								Button uid = new Button();
								uid.setPos(0, i);
								uid.setText(uName);
								if(old.equals(u)) uid.setBackground(Color.green);
								uid.setFill('h');
								uid.setAL(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										if(blacks.get(name).get("uid_" + type).equals(u)) {
											blacks.get(name).put("uid_" + type, "null");
											GUI.setBackground(name + "::spec::" + type + "::" + ii, GUI.getDefButCol());
										} else {
											blacks.get(name).put("uid_" + type, u);
											GUI.setBackground(name + "::spec::" + type + "::" + ii, Color.green);
											GUI.setBackground(name + "::spec::" + type + "::" + ((ii+1)%3), GUI.getDefButCol());
											GUI.setBackground(name + "::spec::" + type + "::" + ((ii+2)%3), GUI.getDefButCol());
										}
									}
								});
								gspec.addBut(uid, name + "::spec::" + type + "::" + i);
							}
							
						}
					});
					unit.addBut(spec);
					
					Label space = new Label();
					space.setPos(0, 2);
					space.setText("");
					space.setSize(1, 20);
					unit.addLabel(space);
					
					units.addContainer(unit);
				}
				
				gui.addContainer(units);
			}
		});
		part.addBut(stngs);
		
		Button del = new Button();
		del.setPos(7, 0);
		del.setSpan(1, 4);
		del.setFill('v');
		del.setText("\uD83D\uDDD1");
		del.setFont(new Font(null, Font.PLAIN, 20));
		del.setAL(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(gui.showConfirmationBox("delete " + name + "?")) {
					forget(name);
				}
			}
		});
		part.addBut(del);
		
		both.addContainer(part);
		
		gui.addContainer(both, name + "::part");
	}
	
	private static String[] rewBefore = new String[] {"gold", "token", "potion", "meat"};
	
	private static String[] getAppFiles(String folderpath) {
		class Filter implements FilenameFilter {
			@Override
			public boolean accept(File dir, String name) {
				if(!name.endsWith(".app")) return false;
				return true;
			}
		}
		
		File folder = new File(folderpath);
		
		return folder.list(new Filter());
	}
	
	public static void refresh() {
		if(gui != null) {
			String[] proFiles = getAppFiles("profiles");
			if(proFiles == null) return;
			for(int i=0; i<proFiles.length; i++) {
				String name = proFiles[i].replace(".app", "");
				if(!profiles.keySet().contains(name)) {
					add(name, new Run(name, NEF.read("profiles/" + proFiles[i]).replace("\n", "; ")));
				}
			}
			gui.refresh();
		}
	}

	public static void close() {
		try {
			gui.close();
		} catch (Exception e) {}
		
		Set<String> keys = profiles.keySet();
		for(String key : keys) {
			NEF.saveOpt("configs/" + key + ".app", blacks.get(key));
			profiles.get(key).setRunning(false);
		}
		
		System.exit(0);
	}
	
	
}
