package com.mycompany.presentation.profile;

import com.mycompany.data.repo_impl.PlayerRepositoryImpl;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Player;

public class ProfileManager {
    private final PlayerRepository playerRepository;
    private String selectedCharId;

    public ProfileManager() {
        this.playerRepository = new PlayerRepositoryImpl();
    }

    public Player getCurrentPlayer() {
        Player player = playerRepository.getCurrentPlayer();
        if (player != null && selectedCharId == null) {
            selectedCharId = player.getAvatar() != null ? player.getAvatar() : "robot";
        }
        return player;
    }

    public String getSelectedCharId() {
        return selectedCharId;
    }

    public void selectChar(String id) {
        this.selectedCharId = id;
    }

    // Returns error message or null if success
    public String saveProfile(String newName, String newPass) {
        boolean success = true;
        StringBuilder errorMessage = new StringBuilder();

        Player currentPlayer = playerRepository.getCurrentPlayer();
        if (currentPlayer == null)
            return "No current player found.";

        // Update Username
        if (newName != null) {
            newName = newName.trim();
        }

        if (newName != null && !newName.isEmpty() && !newName.equals(currentPlayer.getUserName())) {
            Player res = playerRepository.changeUserName(newName);
            if (res == null || res.getId() == 0) {
                success = false;
                errorMessage.append("Failed to update username.\n");
            }
        } else if (newName == null || newName.isEmpty()) {
            return "Username cannot be empty.";
        }

        // Update Password
        if (newPass != null && !newPass.trim().isEmpty()) {
            Player res = playerRepository.changePassword(newPass);
            if (res == null || res.getId() == 0) {
                success = false;
                errorMessage.append("Failed to update password.\n");
            }
        }

        // Update Avatar
        String currentAvatar = currentPlayer.getAvatar();
        // String selectedCharId is already a field in this class, using it directly

        if (selectedCharId != null && !selectedCharId.equals(currentAvatar)) {
            Player res = playerRepository.changeAvatar(selectedCharId);
            if (res == null || res.getId() == 0) {
                success = false;
                errorMessage.append("Failed to update avatar.\n");
            }
        }

        if (success) {
            return null; // Success
        } else {
            return errorMessage.length() == 0 ? "Update failed" : errorMessage.toString();
        }
    }
}
