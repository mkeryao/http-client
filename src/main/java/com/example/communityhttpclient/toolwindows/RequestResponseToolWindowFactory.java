package com.example.communityhttpclient.toolwindows;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RequestResponseToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Main panel for the tool window
        JPanel toolWindowPanel = new JPanel(new BorderLayout());

        // DETAILED REQUEST PANEL (CENTER)
        JPanel detailedRequestPanel = new JPanel(new BorderLayout());

        // Input Panel for URL and Method (NORTH of detailedRequestPanel)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Method:"));
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"};
        JComboBox<String> methodComboBox = new JComboBox<>(methods);
        inputPanel.add(methodComboBox);

        inputPanel.add(new JLabel("URL:"));
        JTextField urlField = new JTextField("https://api.example.com/data", 40);
        inputPanel.add(urlField);

        detailedRequestPanel.add(inputPanel, BorderLayout.NORTH);

        // Tabbed Pane for Headers, Parameters, Body (CENTER of detailedRequestPanel)
        JTabbedPane requestDetailsTabs = new JTabbedPane();

        // --- Headers Tab ---
        JPanel headersPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Key", "Value"};
        DefaultTableModel headersTableModel = new DefaultTableModel(columnNames, 0);
        // Add a default header or leave empty
        headersTableModel.addRow(new Object[]{"Content-Type", "application/json"});
        JTable headersTable = new JTable(headersTableModel);
        headersPanel.add(new JScrollPane(headersTable), BorderLayout.CENTER);

        JPanel headerControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addHeaderButton = new JButton("Add Header");
        addHeaderButton.addActionListener(e -> headersTableModel.addRow(new Object[]{"", ""}));
        headerControlsPanel.add(addHeaderButton);

        JButton removeHeaderButton = new JButton("Remove Selected Header");
        removeHeaderButton.addActionListener(e -> {
            int selectedRow = headersTable.getSelectedRow();
            if (selectedRow != -1) {
                headersTableModel.removeRow(selectedRow);
            }
        });
        headerControlsPanel.add(removeHeaderButton);
        headersPanel.add(headerControlsPanel, BorderLayout.SOUTH);
        requestDetailsTabs.addTab("Headers", headersPanel);

        // --- Parameters Tab ---
        JPanel parametersPanel = new JPanel(new BorderLayout());
        String[] paramsColumnNames = {"Key", "Value"};
        DefaultTableModel paramsTableModel = new DefaultTableModel(paramsColumnNames, 0);
        paramsTableModel.addRow(new Object[]{"id", "123"}); // Example parameter
        JTable paramsTable = new JTable(paramsTableModel);
        parametersPanel.add(new JScrollPane(paramsTable), BorderLayout.CENTER);

        JPanel paramsControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addParamButton = new JButton("Add Parameter");
        addParamButton.addActionListener(e -> paramsTableModel.addRow(new Object[]{"", ""}));
        paramsControlsPanel.add(addParamButton);

        JButton removeParamButton = new JButton("Remove Selected Parameter");
        removeParamButton.addActionListener(e -> {
            int selectedRow = paramsTable.getSelectedRow();
            if (selectedRow != -1) {
                paramsTableModel.removeRow(selectedRow);
            }
        });
        paramsControlsPanel.add(removeParamButton);
        parametersPanel.add(paramsControlsPanel, BorderLayout.SOUTH);

        requestDetailsTabs.addTab("Parameters", parametersPanel);

        // --- Body Tab (Placeholder) ---
        requestDetailsTabs.addTab("Body", new JLabel(" Request Body content will go here (selector + editor) ", SwingConstants.CENTER));

        detailedRequestPanel.add(requestDetailsTabs, BorderLayout.CENTER);

        toolWindowPanel.add(detailedRequestPanel, BorderLayout.CENTER);

        // Placeholder for Response Panel (SOUTH)
        JPanel responsePanel = new JPanel(new BorderLayout());
        JLabel responseLabel = new JLabel("Response Details Will Go Here", SwingConstants.CENTER);
        responsePanel.add(responseLabel, BorderLayout.CENTER);
        responsePanel.setPreferredSize(new Dimension(0, 200));
        toolWindowPanel.add(responsePanel, BorderLayout.SOUTH);

        // Controls Panel (Send button, Env Selector)
        JPanel bottomControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomControlsPanel.add(new JButton("Send"));
        bottomControlsPanel.add(new JLabel("Env: "));
        // JComboBox<String> envComboBox = new JComboBox<>(new String[]{"dev", "prod"}); // Example
        // bottomControlsPanel.add(envComboBox);
        toolWindowPanel.add(bottomControlsPanel, BorderLayout.PAGE_END);


        // Add the main panel to the tool window
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(toolWindowPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
