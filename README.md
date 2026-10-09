# 🧩 Servus Patches (unofficial)

Morphe patches for the ServusTV On Android app.

## ❓ About

Unofficial patches for [ServusTV On](https://play.google.com/store/apps/details?id=com.mautilus.servus) (`com.mautilus.servus`), for phones and Android TV.

- **Disable video ads**: removes the pre-roll and mid-roll ads (Google IMA) from videos and live channels.

### How to use these patches

Click here to add these patches to Morphe: https://morphe.software/add-source?github=Sudashiii/servus-patches

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.0.0-dev.3](https://github.com/Sudashiii/servus-patches/releases/tag/v1.0.0-dev.3)**&nbsp;&nbsp;•&nbsp;&nbsp;`dev`&nbsp;&nbsp;•&nbsp;&nbsp;4 patches total
<details open>
<summary>📦 ServusTV On&nbsp;&nbsp;•&nbsp;&nbsp;4 patches</summary>
<br>

**🎯 Supported versions:**

| 7.5.2.0 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Block forced updates](#block-forced-updates) | Disables Google Play in-app update prompts and makes 'Update required' messages dismissible, so the patched app can't be locked out by a forced update. |  |
| [Disable review prompts](#disable-review-prompts) | Stops the app from asking for a Play Store rating after watching videos or adding favorites. |  |
| [Disable tracking](#disable-tracking) | Makes the app behave as if all tracking consent was declined, so Braze, Datadog, Red Bull analytics, GfK and Datazoom never start and Crashlytics and Firebase Analytics don't collect data. |  |
| [Disable video ads](#disable-video-ads) | Removes pre-roll and mid-roll ads (Google IMA) from videos and live channels. |  |

</details>

<!-- PATCHES_END -->

### 🛠️ Building locally

- Run `./gradlew buildAndroid`
- The built patches .mpp file is found in `patches/build/libs/patches-*.mpp`
- Patch the mpp file using [Morphe-Desktop](https://github.com/MorpheApp/morphe-desktop)
  like any other patch bundle.

See the [Morphe documentation](https://github.com/MorpheApp/morphe-documentation) for more information.

## ⚖️ Disclaimer

- This is an unofficial, independent, non-commercial project. It is **not affiliated with, endorsed, or sponsored by
  ServusTV, Red Bull Media House, or the developers of the ServusTV On app**, nor by the Morphe project.
  "ServusTV" and "ServusTV On" are trademarks of their respective owners and are used here only to describe
  which app these patches work with.
- This repository **does not contain or distribute any code, APKs, assets, or decompiled sources of ServusTV On**,
  and no modified versions of the app are published. It only contains patch instructions, which users apply
  themselves to their own copy of the app.
- The patches **do not circumvent DRM** (Widevine) or any other technical protection measure, and do not unlock
  paid or geo-restricted content.
- Use at your own risk and responsibility. Modifying the app may violate its terms of use. The software is provided
  "as is", without warranty of any kind (see the license).
- Rights holders with concerns can [open an issue](https://github.com/Sudashiii/servus-patches/issues).

## 📜 License

Servus Patches are licensed under the [GNU General Public License v3.0](LICENSE)
