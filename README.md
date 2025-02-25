# Forwardly

Forwardly is an Android application built with Java and Kotlin that automatically forwards incoming messages from an extra phone to your main phone via a Telegram bot. Originally conceived to help a friend in the US receive OTPs sent to her Indian number without incurring expensive international charges, Forwardly now offers a flexible solution for anyone who wants to remotely monitor and manage messages.

## Background & Motivation

The project was inspired by a real-world need: a friend required OTPs (One-Time Passwords) sent to her Indian number while she was residing in the US. Instead of paying high international roaming fees just to receive these OTPs, Forwardly was created as a cost-effective workaround. By installing the app on a secondary phone, incoming messages (including OTPs) are automatically forwarded to her main device via a Telegram bot, ensuring she stays connected without the high expense.

## How It Works

1. **Extra Phone Setup:** Install Forwardly on an extra phone dedicated solely to receiving messages.
2. **Message Reception:** The app continuously monitors the phone for incoming messages (such as OTPs and notifications).
3. **Telegram Bot Forwarding:** Upon receiving a message, Forwardly automatically sends the content to your designated Telegram chat using a Telegram bot.
4. **Real-Time Delivery:** The forwarded message appears on your main phone instantly, ensuring you never miss a crucial OTP or notification.

## Features

- **Automatic Message Forwarding:** Seamlessly forwards incoming messages from your secondary phone.
- **Telegram Bot Integration:** Utilizes the Telegram bot API for secure, real-time message delivery.
- **Cost-Effective Communication:** Designed to bypass expensive international roaming for OTPs and essential notifications.
- **Easy Setup & Configuration:** Simple in-app process to input your Telegram bot token and target chat ID.
- **Open Source & Customizable:** Fully open for modifications, allowing you to tailor the app to your needs.

## Prerequisites

- **Android Device:** A secondary phone running Android (compatible with Android 5.0+ or later).
- **Telegram Account:** To create and manage a bot via [@BotFather](https://t.me/BotFather).
- **Development Tools (for contributors):**
  - Android Studio (or your preferred IDE)
  - Java Development Kit (JDK) 11 or higher
  - Gradle (the project includes a wrapper)


### For Developers

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/indrayudd/forwardly.git
   cd forwardly
