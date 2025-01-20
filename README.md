# Women Safety App

## Overview
The Women Safety App is designed to provide a discreet yet powerful safety tool for women. It acts as a normal calculator but integrates safety features that can be activated in emergencies. The app ensures quick and effective communication with authorities and trusted contacts.

## Problem Statement
Women often face safety concerns in various environments, and there is a need for an accessible, discreet, and effective tool to address emergencies. Existing solutions lack subtlety, making them less effective in critical situations.

## Solution
1. **Discreet Operation:** The app opens as a normal calculator.
2. **Alert Activation:**
   - Pressing "505" triggers alert mode.
   - Speaking "Yo" activates alert mode via voice command.
3. **Emergency Features:**
   - Automatically dials 1091 (Women Helpline).
   - Sends location and SOS messages to:
     - Trusted contacts.
     - 1091 helpline.
4. **Hidden Page:** Long pressing "AC" opens the Women Safety page with additional features:
   - SOS feature (location and message sharing to colleagues and 1091).
   - Quick dial to 1091.
   - Share location and send messages to specific colleagues.
5. **Self-Defense Resources:** A video section provides self-defense training.
6. **Colleague Management:**
   - Add, view, or delete trusted colleagues using Firebase SDK.

## Tech Stack
1. **Backend:** Firebase SDK for database management and authentication.
2. **APIs:**
   - Google Maps API (for location sharing)
   - Google Speech-To-Text API(for Speech recognition)
4. **Emergency Dialing:** Integrated with native phone services.
5. **Multimedia:** Embedded self-defense video resources.

## Demo
1. **Home Screen:** Opens as a calculator.
2. **Activate Alert Mode:**
   - Input "505" to initiate safety protocols.
   - Speak "Yo" to activate safety protocols via voice command.
3. **Women Safety Page:** Long press "AC" to reveal additional safety features.
4. **Features in Action:**
   - Real-time location sharing.
   - SOS messaging.
   - Colleague management.
   - Self-defense video demonstration.

## Challenges and Learnings
### Challenges:
- Integrating safety features without compromising the app’s disguise.
- Ensuring real-time location sharing accuracy.
- Managing emergency communication efficiently.

### Learnings:
- Importance of user-friendly design for quick access.
- Enhanced understanding of Firebase integration.
- Effective use of APIs for real-time functionalities.

## Future Scope
1. **Enhanced Features:**
   - Video command activation for emergencies.
   - Integration with wearable devices.
2. **Expanded Accessibility:**
   - Multilingual support.
   - Availability on more platforms.
3. **Community Engagement:**
   - Safety forums and resources for women.
   - Partnering with self-defense trainers for live sessions.
4. **Data Analytics:**
   - Heatmaps of unsafe areas based on user inputs.

## Conclusion
The Women Safety App combines innovation, discretion, and effectiveness to create a reliable safety tool for women. By leveraging advanced technology, it empowers women to navigate their environments with confidence and security.
