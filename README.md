# BunnyMemoryChannelPointsClient
An application to allow viewers on Twitch to mess with your Game of Rabi-Ribi

# Usage
To launch this, i recommend opening a command prompt and typing ``java -jar memoryEdit.jar``.
Go to the Release tab to download a compiled version.

## Twitch
For users to interact with this application, you need to be Affilliate or Partner and have
Channel Points activated. Effectively, all setup is done through ``[special words in brackets]``
in Custom Channel Points Rewards.

Go to [your Channel Points Settings](https://dashboard.twitch.tv/community/channel-points/rewards)
and add some Custom Rewards. You can change the Title to be whatever you like that advertises
the feature well enough. Set the price to whatever you like or think is appropriate.
The important part will be the **Description** of the Custom Reward - it must always start with
**``[BUNNY]``**. Depending on what effect you want, add some more Tags directly after that. Which
Tags are supported you can see in the below Table.

After the Tags, you can put whatever you like, so the viewer knows what they are getting into.

**Note:** I recommend enabling the option to skip the Reward Queue as the App currently
ignores this setting and instantly applies whatever is triggered by the Tag, even if it only
got added to the Queue for review.

## Available Tags
| Tag | Needs User Input | Description|
|-----|:---:|----|
| [HP] | <ul><li>- [x]  </li></ul>|Sets your current HP to whatever value the Viewer entered|
| [FULLHEAL] | <ul><li>- [ ] </li></ul>|Fully heals you|
