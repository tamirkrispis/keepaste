# Keepaste

### The Keep & Paste application
Boost your productivity!<br>
Keepaste is a cross-platform application that simplifies the process of storing and pasting frequently used commands and text phrases.<br>
No more time-consuming searches for that command syntax you used recently or that DB query you keep on forgetting or repeatedly 
retyping the same commands over and over again on your day-to-day work.<br>
With Keepaste, you can save time and effort by quickly accessing your stored text and pasting it on any other window with a single click. 
It's designed to be intuitive and efficient. <br>
Moreover, instead of executing multiple commands in order to construct the final desired one, have everything in one go thanks to
the powerful dynamic parameters capabilities.<br>
Main use case - keeping command lines and their arguments and executing those on a terminal, IDE on any other window upon need.

## Introduction

In a world where command-lines tools for the ever-growing number of technologies play a crucial role in software development (aws, docker, java, kafka,
kubectl, mvn, terraform, to name a few), there is a need for a commands management tools, this is where Keepaste becomes
an indispensable tool in your arsenal.
It simplifies your workflow, centralizes your commands, simplifies a wide range of technologies, and offers customization 
options to match your preferences. By using Keepaste, you will experience a more efficient and productive way of managing
and executing commands across different technologies.

Streamline your workflow, save time, and unleash your true potential as a developer, give it a go, and you won't look back!

## Main Features
- **Easy Text Storage**: Store an unlimited number of text phrases (named 'Keeps') in Keepaste's user-friendly interface. Organize your phrases into categories for easy management and retrieval.

- **Automated Paste**: Will automatically switch to a desired window, paste your Keep (text phrase) and press the 'Enter' key with a click of a button. Whether you're coding, analysing data, writing emails, or chatting with friends, Keepaste ensures you never have to retype the same content repeatedly.

- **Dynamic Parameters**: Instead of multiple Keeps (phrases/commands) for different situations, uou can have a single generic Keep which holds one or more dynamically set parameters. An extremely powerful feature!

- **Import / Export**: Expand, Share and Backup your Keeps by using an easy export/import features.

- **Customizable Shortcuts**: (Future feature) Assign personalized shortcuts to your most frequently used phrases for quick and efficient access. Say goodbye to manual typing and boost your productivity.

- **Sync and Backup**: (Future feature) Keepaste keeps your data synchronized across all your devices, allowing you to access your stored text phrases from anywhere. Automatic backup functionality ensures your data is safe and protected.

This documentation provides instructions on how to use Keepaste effectively.

## Download and Installation

### System Requirements

- Supported Operating Systems: Windows, macOS (Linux coming soon as well)
- No special system requirements, it is pretty lean

### Download and Installation

1. Visit the Keepaste website at [www.keepaste.com](https://www.keepaste.com) or the [official GitHub repository](http://www.github.com/tamirkrispis/keepaste).

2. Download the package suitable for your operating system.

3. Run it and follow any on-screen instructions.

## Getting Started

### User Interface Overview
You will notice that using Keepaste is quite intuitive and easy. You will just need to manage your Keeps tree.<br>
Right-click with your mouse on any tree node in order to reveal the context menu with a list of actions you can perform.<br>
The tree comes with some example commands in order to better understand the capabilities and usage of Keepaste, you can delete those after you got the hang of it.

Hovering with the mouse over a Keep node will show you a tooltip with more information about that Keep.

### Executing a Keep
While working on your computer, notice the bottom status-line of the Keepaste application while you switch between windows.
It will change as you switch between different windows and applications. This is your set active window (you can also lock on one window, we'll show that later).
<br>Now, let's say you have a Keep which holds a command-line command, and your active window is a terminal of some sort, by 
double-clocking on that Keep node on the tree Keepaste will move to the active-window, paste your Keep and will press 'Enter' automatically.
<br>It is quite addictive... but it is a good addiction :)

### Import / Export
Any Keep or a group of Keeps can be exported into a simple and readable file in the JSON format by right-clicking on a node and selecting the Export option.
<br>This file can be used as a Backup file, a file to Share with your co-workers or friends, or to share on the Keeps Library (see section below).
<br>To import the file, just right-click with your mouse on the tree where you want it to and select the Import option.

### Keeps Library
Working with AWS? Azure? GCP? Docker? K8S? Terraform or any other technology?<br>
Save some time by visiting the online [Keeps Library](https://www.github.com/tamirkrispis/keeps-library) repository (which is also open-source).
You can download and import common presets of Keeps based on your needs or contribute by sharing or adding to the existing Keeps collections.
<br> It is far from being complete, and although it can be used as is, it should be treated as a starting point to be modified and enhanced based on your needs.

### Keep's Parameters (<- worth reading)
In order to take the full advantage out of Keepaste, make sure you're aware of these strong features, a Keep shouldn't always be 
a static one, meaning, a simple text phrase representing a command.<br>
Such use-case, for using one or more parameters, could be having the same command, but for different environments, or namespaces or process names, etc. You 
can define a single Keep with parameter(s) instead of one Keep per environment, namespace etc.<br>
This becomes possible with Keep Parameters, when creating a Keep you can set parts in triangular brackets holding a name of a parameter,
ex. <i>echo "Hello &lt;name&gt;"</i>, in this case name becomes a parameter that should be set once this Keep is executed.

There are 3 types of Keep Parameters -
#### 1. Free Text 
When editing the Keep, on the Edit Keep Dialog, the 'Parameter Value' for that parameter on the parameters table should be left blank.
#### 2. Set of Predefined Values
The 'Parameter Value' should be set as [Option1,Option2,...].
#### 3. Command
Write any command to be executed before pasting the Keep and have the output lines represented as a dropdown to choose from (use-case examples: K8S namespaces or pod names, etc.).<br>
This is a very powerful feature to exploit and as time goes by you will see there are vast opportunities to use it.

## Settings
The below settings are available, once changed it is automatically saved and will hold when you close and reopen Keepaste.

<b>Always on top</b><br>
will keep the Keepaste application on top of all other windows on the screen.<br>

<b>Flow</b><br>
You can control how Keepaste will act once a Keep is being executed - 
1. <b>Copy keep to clipboard</b><br>When this is the only selected option, Keepaste will enter the 'only-copy' mode, where it will
only copy the phrase (it will manipulate parameters if any) but will not switch to the active-window or paste the complete phrase.
<br><b>Use-case:</b> production or delicate environments where you want to only have the phrase ready to paste on the clipboard wherever you need it.
2. <b>Focus on target window and paste</b><br>Keepaste will enter the 'Paste-but-do-not-run' mode, it will perform section 1 (get the command ready on the clipboard) but will also switch to
the target window and will paste the command on it.
<br><b>Use-case:</b> production or delicate environments where you want the treat of having this automation, but you want to
review the pasted command before execution.
3. <b>Press 'Enter'</b><br>Keepaste will enter the 'full-on' mode, where it will perform both sections 1 and 2 and will also 
click the 'Enter' key for you in order to execute the pasted phrase on the target window.
<br><b>Use-case:</b> day-to-day work, on any environment, once you have a built confidence in your Keeps tree.
 
<b>Themes</b><br>
Keepaste supports themes for the GUI, you can switch between Light and Dark themes.  

[//]: # (## Troubleshooting)

[//]: # ()
[//]: # (### Frequently Asked Questions)

[//]: # ()
[//]: # (- Q: How do I paste a Keep using the double-click mouse button feature?)

[//]: # (    - A: Simply double-click the assigned shortcut key of the desired Keep, and it will be pasted at the current cursor position.)

[//]: # ()
[//]: # (- Q: Can I import or export my Keeps in Keepaste?)

[//]: # (    - A: Currently, Keepaste does not provide built-in import/export functionality. However, you can manually copy and paste Keeps between instances of Keepaste.)

## Known Issues and Workarounds

- Issue: On Mac, when I double-click a Keep to execute it, it doesn't switch to the active window.
    - Workaround: Change the settings using the top menu so Keepaste won't be always on top and give it another go.
- Issue: On Windows, Keepaste won't identify some of my windows.
  - Workaround: These windows may run in higher privileges ("Run as administrator"), all you need to do is also run Keepaste as an administrator (Right-click on the Keepaste executable and choose "Run as administrator") and it will be able to get those as well.

## Contribution, Support and Feedback
Keepaste was built as a side project to support my day to day software development work, due to that, it currently lacks things like comprehensive commenting, testing, and some nice-to-have features.
I will keep adding stuff to it and have it more mature as time goes by based on core needs and my free time.
Any contributions from the community to enhance Keepaste are most welcome.
If you have any ideas, bug reports, or feature requests, please visit our GitHub repository at [github.com/tamirkrispis/keepaste/issues](https://github.com/tamirkrispis/keepaste/issues) 
to create a new issue with detailed information about the problem. Your feedback and support are much appreciated!
For anything else, feel free to drop me an email at [tamirkrispis@gmail.com](mailto:tamirkrispis@gmail.com).

If you like Keepaste, or better off, don't, it would be very helpful to let me know about it and how I can make things better in the future.

Fuel this open-source project with a [cup of coffee!](https://www.buymeacoffee.com/keepaste) ☕ to show your support and keep Keepaste moving forward.

<b>Important note:</b> The icons used for the application are licensed and provided by <a href="https://icons8.com/">Icons8</a>, hence extraction and re-use of icons is prohibited.


# Happy Keep & Pasting!