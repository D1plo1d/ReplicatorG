/*
 CncOverNetDriver.java

 This is a driver to control a machine that uses the CncOverNet TCP/IP interface.

 Part of the ReplicatorG project - http://www.replicat.org
 Copyright (c) 2010 Rob Gilson

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/* Additional: Sorry this is copy pasta - I fear some day, that some poor bastard 
 * will need to fix that but it looks like that would involve a far greater 
 * rewrite of ReplicatorG drivers then I currently have time to do.
 * 	- Rob
 */

package replicatorg.drivers.cncOverNet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import org.w3c.dom.Node;

import replicatorg.drivers.NetworkDriver;
import replicatorg.drivers.OnboardParameters;
import replicatorg.drivers.SDCardCapture;
import replicatorg.drivers.PenPlotter;
import replicatorg.drivers.Version;
import replicatorg.machine.model.Axis;

public class CncOverNetDriver extends NetworkDriver
	implements OnboardParameters, SDCardCapture, PenPlotter
{
	Version toolVersion = new Version(0,0);
	
	public CncOverNetDriver() {
		super();

		// init our variables.
		setInitialized(false);
	}

	public void loadXML(Node xml) {
		super.loadXML(xml);

	}

	public void initialize() {
		if (isInitialized()) return;

		// Assert: serial port present.
		assert socket != null : "No socket connection.";

		//TODO: get firmware name and version!
		String firmwareName = "BS//test";
		//int firmwareVersion = 0;
		
		if (firmwareName !=null)
		{
			// it worked! great - now let's do some setup
			invalidatePosition();
			super.initialize();
		}
/*		else
		{
			Base.logger.log(Level.INFO,"Unable to connect to firmware.");
			// Dispose of driver to free up any resources
			dispose();
		}
*/
	}

	/**
	 * TODO:Sends the command over the serial connection and retrieves a result.
	 */
/*	protected Object runCommand(Object command) {

	}*/

	
	/**=============================================================== 
	 * And Now: The terrifying tail of the endless Replicator G API
	 * =============================================================== */
	
	@Override
	public void setServoPos(double degree) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseCode beginCapture(String filename) {
		// TODO Auto-generated method stub
		return ResponseCode.SUCCESS;
	}

	@Override
	public int endCapture() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void beginFileCapture(String path) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endFileCapture() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseCode playback(String filename) {
		// TODO Auto-generated method stub
		return ResponseCode.SUCCESS;
	}

	@Override
	public boolean hasFeatureSDCardCapture() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getFileList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumSet<Axis> getInvertedParameters() {
		// TODO Auto-generated method stub
		return EnumSet.noneOf(Axis.class);
	}

	@Override
	public void setInvertedParameters(EnumSet<Axis> axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMachineName() {
		// TODO Auto-generated method stub
		return "DEV/NULL/BS";
	}

	@Override
	public void setMachineName(String machineName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EndstopType getInvertedEndstops() {
		// TODO Auto-generated method stub
		return EndstopType.NOT_PRESENT;
	}

	@Override
	public void setInvertedEndstops(EndstopType endstops) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasFeatureOnboardParameters() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createThermistorTable(int which, double r0, double t0,
			double beta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getR0(int which) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getT0(int which) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBeta(int which) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BackoffParameters getBackoffParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBackoffParameters(BackoffParameters params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PIDParameters getPIDParameters(int which) {
		// TODO Auto-generated method stub
		return new PIDParameters();
	}

	@Override
	public void setPIDParameters(int which, PIDParameters params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetToFactory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetToolToFactory() {
		// TODO Auto-generated method stub
		
	}

}