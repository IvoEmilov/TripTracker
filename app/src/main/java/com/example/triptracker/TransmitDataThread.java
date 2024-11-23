package com.example.triptracker;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DtcNumberCommand;
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand;
import com.github.pires.obd.commands.control.PermanentTroubleCodesCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_01_20;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_21_40;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_41_60;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransmitDataThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice device;
    MainActivity activity;
    UpdateUIThread UIThread;
    DataCalculations calc;

    public static boolean dtcFlag = Boolean.FALSE;

    SpinnerTxItem spinnerTxItem1 = new SpinnerTxItem();
    SpinnerTxItem spinnerTxItem2 = new SpinnerTxItem();
    Map<String, ObdCommand> commandsMap
            = new HashMap<>();

    AirIntakeTemperatureCommand AirIntakeTemperature = new AirIntakeTemperatureCommand();
    AmbientAirTemperatureCommand AmbientAirTemperature = new AmbientAirTemperatureCommand();
    EngineCoolantTemperatureCommand EngineCoolantTemperature = new EngineCoolantTemperatureCommand();
//    BarometricPressureCommand BarometricPressure = new BarometricPressureCommand();
//    FuelPressureCommand FuelPressure = new FuelPressureCommand();
//    FuelRailPressureCommand FuelRailPressure = new FuelRailPressureCommand();
//    IntakeManifoldPressureCommand IntakeManifoldPressure = new IntakeManifoldPressureCommand();
//    AirFuelRatioCommand AirFuelRatio = new AirFuelRatioCommand();
    ConsumptionRateCommand ConsumptionRate = new ConsumptionRateCommand();
//    FindFuelTypeCommand FindFuelType = new FindFuelTypeCommand();
//    FuelLevelCommand FuelLevel = new FuelLevelCommand();
//    FuelTrimCommand FuelTrim = new FuelTrimCommand();
//    WidebandAirFuelRatioCommand WidebandAirFuelRatio = new WidebandAirFuelRatioCommand();
//    AbsoluteLoadCommand AbsoluteLoad = new AbsoluteLoadCommand();
//    LoadCommand Load = new LoadCommand();
    MassAirFlowCommand MassAirFlow = new MassAirFlowCommand();
    OilTempCommand OilTemp = new OilTempCommand();
    RPMCommand RPM = new RPMCommand();
    ThrottlePositionCommand ThrottlePosition = new ThrottlePositionCommand();
//    DistanceMILOnCommand DistanceMILOn = new DistanceMILOnCommand();
//    DistanceSinceCCCommand DistanceSinceCC = new DistanceSinceCCCommand();

//    EquivalentRatioCommand EquivalentRatio = new EquivalentRatioCommand();
//    IgnitionMonitorCommand IgnitionMonitor = new IgnitionMonitorCommand();
//    ModuleVoltageCommand ModuleVoltage = new ModuleVoltageCommand();
    PendingTroubleCodesCommand PendingTroubleCodes = new PendingTroubleCodesCommand();
    PermanentTroubleCodesCommand PermanentTroubleCodes = new PermanentTroubleCodesCommand();
//    TimingAdvanceCommand TimingAdvance = new TimingAdvanceCommand();

    SpeedCommand Speed = new SpeedCommand();
    VinCommand Vin = new VinCommand();

    ArrayList<SpinnerTxItem> commandsList = new ArrayList<>();



    public TransmitDataThread(BluetoothSocket mmSocket, MainActivity activity, BluetoothDevice device) {
        this.mmSocket = mmSocket;
        this.activity = activity;
        this.device = device;

        calc = new DataCalculations(activity);
        UIThread = new UpdateUIThread(activity);
    }

    public void run() {
        boolean speedFlag;
        boolean mafFlag;

        UIThread.establishedConnectionUI(device.getName());
        populateCommandsMap();
        UIThread.closeConnection(mmSocket, this, Boolean.FALSE);

        /*Create list of commands to poll everything and see which are available in the Ibiza*/
        AvailablePidsCommand_01_20 AvailablePids_01_20 = new AvailablePidsCommand_01_20();
        AvailablePidsCommand_21_40 AvailablePids_21_40 = new AvailablePidsCommand_21_40();
        AvailablePidsCommand_41_60 AvailablePids_41_60 = new AvailablePidsCommand_41_60();

        try {
            new EchoOffCommand().run(mmSocket.getInputStream(), mmSocket.getOutputStream());
            new LineFeedOffCommand().run(mmSocket.getInputStream(), mmSocket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(mmSocket.getInputStream(), mmSocket.getOutputStream());

            AvailablePids_01_20.run(mmSocket.getInputStream(), mmSocket.getOutputStream());
            AvailablePids_21_40.run(mmSocket.getInputStream(), mmSocket.getOutputStream());
            AvailablePids_41_60.run(mmSocket.getInputStream(), mmSocket.getOutputStream());


            LogWriter.write("[PIDs] ", AvailablePids_01_20.getName());
            LogWriter.write("[PIDs] ", AvailablePids_01_20.getResult());
            LogWriter.write("[PIDs] ", AvailablePids_01_20.getFormattedResult());
            LogWriter.write("[PIDs] ", AvailablePids_21_40.getName());
            LogWriter.write("[PIDs] ", AvailablePids_21_40.getResult());
            LogWriter.write("[PIDs] ", AvailablePids_21_40.getFormattedResult());
            LogWriter.write("[PIDs] ", AvailablePids_41_60.getName());
            LogWriter.write("[PIDs] ", AvailablePids_41_60.getResult());
            LogWriter.write("[PIDs] ", AvailablePids_41_60.getFormattedResult());

            //Thread.sleep(5000);
            while (true)
            {
                if (Thread.currentThread().isInterrupted()) {
                    //TODO: Return data, that have to be stored as a trip.
                    Trip trip = new Trip(calc.getAvgFuel(), calc.getAvgSpeed(), calc.getDistanceTravelled(), calc.getElapsedTime(), DataCalculations.getFuelPrice());
                    UIThread.showTripSummary(trip);
                    break;
                }

                getSelectedCommandItems();
                try{
                    speedFlag = Boolean.FALSE;
                    mafFlag = Boolean.FALSE;

                    for(SpinnerTxItem item : commandsList){
                        UIThread.dataUpdate(item.getSpinnerId() ,executeCommand(item.getCommand()));
                        if(item.getCommand().getClass() == Speed.getClass()){
                            speedFlag = Boolean.TRUE;
                        }
                        if(item.getCommand().getClass() == MassAirFlow.getClass()){
                            mafFlag = Boolean.TRUE;
                        }
                    }
                    if(speedFlag == Boolean.FALSE){
                        executeCommand(Speed);
                    }
                    if(mafFlag == Boolean.FALSE){
                        executeCommand(MassAirFlow);
                    }
                    if(dtcFlag == Boolean.TRUE){
                        DtcNumberCommand DtcNumber = new DtcNumberCommand();
                        TroubleCodesCommand TroubleCodes = new TroubleCodesCommand();
                        executeCommand(DtcNumber);
                        if(DtcNumber.getTotalAvailableCodes() >0){
                            executeCommand(TroubleCodes);
                        }
                        dtcFlag = Boolean.FALSE;
                        UIThread.showDTCReport(DtcNumber, TroubleCodes);
                    }
                }
                catch (Exception e){
                    LogWriter.writeError("[Exception_TD-Thread] ", e.toString());
                }

                UIThread.calculationsUpdate(calc.calculateSpeed(Speed), calc.calculateFuel(Speed, MassAirFlow),
                        calc.getAvgFuelFormatted(), String.valueOf(calc.getDistanceTravelled()));
                commandsList.clear();

                //Thread.sleep(100);
            }
        } catch (Exception e) {
            LogWriter.writeError("[Exception_TD-Thread] ", e.toString());
        }

    }


    void populateCommandsMap(){
        String[] commandsArrayResource = activity.getResourcesArray();
        commandsMap.put(commandsArrayResource[0], Speed);
        commandsMap.put(commandsArrayResource[1], RPM);
        commandsMap.put(commandsArrayResource[2], EngineCoolantTemperature);
        commandsMap.put(commandsArrayResource[3], MassAirFlow);
        commandsMap.put(commandsArrayResource[4], ThrottlePosition);
        commandsMap.put(commandsArrayResource[5], OilTemp);
    }

    void getSelectedCommandItems(){
        spinnerTxItem1.setCommand(commandsMap.get(activity.data1Spinner.getSelectedItem().toString()));
        spinnerTxItem1.setSpinnerId(activity.data1Spinner.getId());
        commandsList.add(spinnerTxItem1);

        spinnerTxItem2.setCommand(commandsMap.get(activity.data2Spinner.getSelectedItem().toString()));
        spinnerTxItem2.setSpinnerId(activity.data2Spinner.getId());
        commandsList.add(spinnerTxItem2);
    }

    private String executeCommand(ObdCommand command){
        try{
            command.run(mmSocket.getInputStream(), mmSocket.getOutputStream());
        }
        catch (IOException e){
            if(e.toString().contains("socket closed")){
                UIThread.closeConnection(mmSocket, this, Boolean.TRUE);
            }
            else{
                LogWriter.writeError("[Command] ", command.getName());
                LogWriter.writeError("[DID] ", command.getCommandPID());
                LogWriter.writeError("[Error_ExecuteCmd] ", e.toString());
                return "Error reading value";
            }
        }
        catch (Exception e){
            LogWriter.writeError("[Command] ", command.getName());
            LogWriter.writeError("[DID] ", command.getCommandPID());
            LogWriter.writeError("[Error_ExecuteCmd] ", e.toString());
            return "Error reading value";
        }
        LogWriter.write("[Command][RX/RAW] ", String.format("[%s] %s / %s", command.getName(),command.getFormattedResult(), command.getResult()));
        return command.getFormattedResult();
    }
}
