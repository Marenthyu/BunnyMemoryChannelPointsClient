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
| The_Tag_you_have_to_put_after_[BUNNY] | Needs_User_Input | Description |
|------------------------------------|:----------------:|-------------|
| [HP] | <ul><li>- [x]  </li></ul>|Sets your current HP to whatever value the Viewer entered|
| [FULLHEAL] | <ul><li>- [ ] </li></ul>|Fully heals you*|
| [HEAL] | <ul><li>- [x]  </li></ul> | Heals you by the amount specified by the Viewer*|
| [DAMAGE] | <ul><li>- [x]  </li></ul> | Damages you by the amount specified by the Viewer|
| [KILL] | <ul><li>- [x]  </li></ul> | Kills you!|
| [HPUP][ADD] | <ul><li>- [ ] </li></ul> | Adds one of the unused Max HP Ups to your inventory, effectively increasing your Max HP.|
| [HPUP][REMOVE] | <ul><li>- [ ] </li></ul> | Removes one of the unused Max HP Ups from your inventory, effectively decreasing your Max HP (if you had one of the unused HP Ups applied before.)|
| [BUFF][BUFF_NAME][DURATION]| Depends | Applies a Buff or Debuff to you. See Table below for valid BUFF_NAMEs. Valid durations are ``REMOVE`` (to remove it), ``INSTANT``, ``SHORT``, ``MEDIUM``, ``LONG`` and ``VERYLONG``. If you set the duration to be ``CUSTOM``, the buff will be applied for the duration the viewer entered.|
| [MONEY][ADD][VALUE] | Depends | Gives you the amount of money specified by ``VALUE``. If ``VALUE`` is ``CUSTOM``, you get the amount of money specified by the viewer instead.|
| [MONEY][REMOVE][VALUE] | Depends | Removes the amount of money specified by ``VALUE`` from you. If ``VALUE`` is ``CUSTOM``, you lose the amount of money specified by the viewer instead.|
| [MONEY][SET][VALUE] | Depends | Just like the above two, just that it ``SET``s your money to the ``VALUE`` specified. May be ``CUSTOM``.|
| [BADGE][RANDOM] | <ul><li>- [ ] </li></ul> | Randomizes your entire Badge Inventory, randomly removing, deleting and equipping badges. Does not care about the Badge Point limit. |
| [BADGE][DELETE][NAME] | <ul><li>- [ ] </li></ul> | Entirely deletes the badge of name ``NAME`` from your inventory. See table below for valid ``NAME``s.|
| [BADGE][UNLOCK][NAME] | <ul><li>- [ ] </li></ul> | Unlocks the badge of name ``NAME`` for you. Unequips the Badge if it was equipped! See table below for valid ``NAME``s.|
| [BADGE][EQUIP][NAME] | <ul><li>- [ ] </li></ul> | Equips the badge of name ``NAME`` for you. Unlocks it if it was not unlocked before. See table below for valid ``NAME``s.|
| [KEYS][SWAP][KEY1][KEY2][DURATION] | Depends | Swaps the two specified ``KEY``s for ``DURATION`` seconds. ``DURATION`` may be ``CUSTOM``. See above. See below for valid ``KEY`` Names. Note that it can get pretty messy with multiple keyswaps for the same keys. Once all Key Swaps expire, the original Key Config will be restores.|

*Note: If you are killed and are still on the death screen, healing you will technically revive you!

## Available ``BUFF`` Names
| Name |
|:---:|
|SPEED_DOWN|
|NUMB|
|POISON|
|ATTACK_DOWN|
|DEFENSE_DOWN|
|CURSED|
|STUNNED|
|BAN_SKILL|
|MANA_DOWN|
|FREEZE|
|BURN|
|ATTACK_UP|
|DEFENSE_UP|
|HP_RECOVER|
|SP_RECOVER|
|SHRINK|
|GIANT|
|ARREST|
|SPEED_UP|
|HALO|
|BADGE_COPY|
|NULL_MEELE|
|DEFENSE_BOOST|
|DEFENSE_DROP|
|STAMINA_DOWN|
|NULL_SLOW|
|SUPER_ARMOUR|
|QUAD_DAMAGE|
|DOUBLE_DAMAGE|
|SPEEDY|
|MAXHP_UP|
|MAXMP_UP|
|AMULET_CUT|
|HP_REGEN|
|MP_REGEN|
|GIVE_ATK_DOWN|
|GIVE_DEF_DOWN|
|UNSTABLE|
|BOOST_FAIL|
|HEX_CANCEL|
|LUCKY_SEVEN|
|QUICK_REFLEX|
|DEFENSE_BOOST_PLUS|
|ENDURANCE|
|FATIGUE|
|HALO_BOOST_1|
|HALO_BOOST_2|
|HALO_BOOST_3|
|99_REFLECT|
|SURVIVAL_INSTINCT|
|AMULET_DRAIN|
|MORTALITY|
|NO_BADGES|
|INSTANT_DEATH|
|HEALTH_ABSORB|
|POWER_ABSORB|
|300_REVENGE|
|BUNNY_LOVER|
|HEALING|
|T_MINUS_TWO|
|T_MINUS_ONE|
|ATTACK_BOOST|
|MEOW_RESPAWN|
|NULL|

## Available ``BADGE`` Names
| Name |
|:---:|
|HEALTH_UP|
|HEALTH_SURGE|
|MANA_UP|
|MANA_SURGE|
|CRISIS_BOOST|
|ATTACK_GROW|
|DEFENSE_GROW|
|ATTACK_TRADE|
|DEFENSE_TRADE|
|ARM_STRENGTH|
|CARROT_BOOST|
|WEAKEN|
|SELF_DEFENSE|
|ARMORED|
|LUCKY_SEVEN|
|HEX_CANCEL|
|PURE_LOVE|
|TOXIC_STRIKE|
|FRAME_CANCEL|
|HEALTH_WAGER|
|MANA_WAGER|
|STAMINA_PLUS|
|BLESSED|
|HITBOX_DOWN|
|CASHBACK|
|SURVIVAL|
|TOP_FORM|
|TOUGH_SKIN|
|ERINA_BADGE|
|RIBBON_BADGE|
|AUTO_TRIGGER|
|LILITHS_GIFT|

## Available ``KEY`` Names
| Name |
|:---:|
|UP|
|DOWN|
|LEFT|
|RIGHT|
|JUMP|
|MAGIC_ATTACK|
|MEELEE_ATTACK|
|BOOST_ATTACK|
|CHANGE_MAGIC_TYPE_LEFT|
|CHANGE_MAGIC_TYPE_RIGHT|
|ITEM_MENU|
|AMULET|
|DASH|