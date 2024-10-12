package com.example.triptracker;

import com.github.pires.obd.commands.ObdCommand;

public class SpinnerTxItem {
    private ObdCommand command;
    private int spinnerId;

    public SpinnerTxItem(ObdCommand command, int spinnerId) {
        this.command = command;
        this.spinnerId = spinnerId;
    }

    public SpinnerTxItem(){}

    public ObdCommand getCommand() {
        return command;
    }

    public int getSpinnerId() {
        return spinnerId;
    }

    public void setCommand(ObdCommand command) {
        this.command = command;
    }

    public void setSpinnerId(int spinnerId) {
        this.spinnerId = spinnerId;
    }
}
